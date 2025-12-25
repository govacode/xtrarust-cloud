package com.xtrarust.cloud.jep.core;

import jep.SharedInterpreter;
import lombok.extern.apachecommons.CommonsLog;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@CommonsLog
public class SingleThreadJepExecutor extends AbstractJepExecutor {

    private final ExecutorService executor;

    private SharedInterpreter interpreter;

    private final AtomicLong lastTaskEndTime = new AtomicLong(System.nanoTime());

    private volatile boolean isShuttingDown = false;

    public SingleThreadJepExecutor(JepExecutorGroup parent, ThreadFactory threadFactory) {
        this(parent, threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public SingleThreadJepExecutor(JepExecutorGroup parent, ThreadFactory threadFactory, RejectedExecutionHandler rejectedHandler) {
        super(parent);
        this.executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory, rejectedHandler);
        CountDownLatch latch = new CountDownLatch(1);
        executor.execute(() -> {
            try {
                this.interpreter = new SharedInterpreter();
            } finally {
                latch.countDown();
            }
        });
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("JEP initialization timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("JEP init interrupted", e);
        }
    }

    @Override
    public JepExecutor next() {
        return this;
    }

    @Override
    public <T> CompletableFuture<T> submit(PythonTask<T> pythonTask) {
        if (isShuttingDown) {
            throw new IllegalStateException("Jep executor is shutting down");
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                long start = System.currentTimeMillis();
                T result = pythonTask.run(interpreter);
                log.info("python script execution end, elapsed: " + (System.currentTimeMillis() - start) + "ms");
                return result;
            } catch (Exception e) {
                throw new PythonTaskFailedException("Python script execution failed", e);
            } finally {
                // 任务执行完，更新静默期计时起点
                lastTaskEndTime.set(System.nanoTime());
            }
        }, this.executor);
    }

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        this.isShuttingDown = true;
        long quietPeriodNanos = unit.toNanos(quietPeriod);
        long timeoutNanos = unit.toNanos(timeout);
        long deadline = System.nanoTime() + timeoutNanos;

        // 1. 尝试进入静默期观察周期
        while (true) {
            long now = System.nanoTime();
            // 如果空闲时间超过了静默期，或者整体到了截止时间，则跳出观察
            if (now - lastTaskEndTime.get() >= quietPeriodNanos || now >= deadline) {
                break;
            }

            // 等待一小段时间继续观察
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 2. 真正关闭：先停止接收新任务，再等待旧任务完成
        executor.execute(() -> {
            try {
                if (interpreter != null) {
                    interpreter.close();
                }
            } catch (Exception e) {
                // ignore
            }
        });
        executor.shutdown();
        try {
            // 等待线程池彻底终止
            long remaining = deadline - System.nanoTime();
            if (!executor.awaitTermination(Math.max(0, remaining), TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }
}
