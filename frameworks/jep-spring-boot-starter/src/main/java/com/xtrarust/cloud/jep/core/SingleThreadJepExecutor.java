package com.xtrarust.cloud.jep.core;

import jep.*;
import lombok.extern.apachecommons.CommonsLog;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@CommonsLog
public class SingleThreadJepExecutor extends AbstractJepExecutor {

    private static final int FULL_GC_THRESHOLD = 1000;

    private static final String CLEANUP_FN = "__jep_internal_cleanup__";

    public static final String VALIDATE_FN = "__jep_internal_validate__";

    private static final String INIT_SCRIPT = """
            import sys, gc
            __JEP_WHITE_LIST__ = set(globals().keys()) | {'__JEP_WHITE_LIST__', '__jep_internal_cleanup__', '__jep_internal_validate__'}

            def __jep_internal_cleanup__():
                v_white = globals().get('__JEP_WHITE_LIST__')
                if not v_white:
                    return
                for k in list(globals().keys()):
                    if k not in v_white:
                        try:
                            del globals()[k]
                        except:
                            pass
            def __jep_internal_validate__(code):
                try:
                    compile(code, '<string>', 'exec')
                    return None
                except SyntaxError as e:
                    return {
                        "lineno": e.lineno,
                        "offset": e.offset,
                        "msg": e.msg,
                        "text": e.text.strip() if e.text else ""
                    }
            """;

    private final ExecutorService executor;

    private Jep interpreter;

    private final AtomicInteger taskCounter = new AtomicInteger(0);

    private final AtomicLong lastTaskEndTime = new AtomicLong(System.nanoTime());

    private volatile boolean isShuttingDown = false;

    public SingleThreadJepExecutor(JepExecutorGroup parent, boolean useSubInterpreter, JepConfig config, ThreadFactory threadFactory) {
        this(parent, useSubInterpreter, config, threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public SingleThreadJepExecutor(JepExecutorGroup parent, boolean useSubInterpreter, JepConfig config, ThreadFactory threadFactory, RejectedExecutionHandler rejectedHandler) {
        super(parent);
        this.executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory, rejectedHandler);
        CountDownLatch latch = new CountDownLatch(1);
        this.executor.execute(() -> {
            try {
                Jep jep;
                if (useSubInterpreter) {
                    jep = new SubInterpreter(config);
                } else {
                    jep = new SharedInterpreter();
                }
                this.interpreter = jep;
                this.interpreter.exec(INIT_SCRIPT);
            } catch (JepException e) {
                throw new RuntimeException("Jep initialization failed", e);
            } finally {
                latch.countDown();
            }
        });
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Jep initialization timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Jep initialization interrupted", e);
        }
    }

    @Override
    public JepExecutor next() {
        return this;
    }

    @Override
    public <T> CompletableFuture<T> submit(PythonTask<T> pythonTask) {
        if (this.interpreter == null) {
            throw new PythonTaskFailedException("Jep interpreter not initialized, please check python environment or java library path");
        }
        if (isShuttingDown) {
            throw new PythonTaskFailedException("Jep executor is shutting down");
        }
        return CompletableFuture.supplyAsync(() -> {
            boolean fullGC = false;
            try {
                int taskCount = taskCounter.incrementAndGet();
                T result = pythonTask.run(interpreter);
                if (taskCount >= FULL_GC_THRESHOLD) {
                    fullGC = true;
                    taskCounter.set(0);
                }
                return result;
            } catch (Exception e) {
                throw new PythonTaskFailedException("Python script execution failed", e);
            } finally {
                cleanup(fullGC);
                // 任务执行完，更新静默期计时起点
                lastTaskEndTime.set(System.nanoTime());
            }
        }, this.executor);
    }

    private void cleanup(boolean fullGC) {
        try {
            this.interpreter.invoke(CLEANUP_FN);
            if (fullGC) {
                this.interpreter.exec("gc.collect(2)");
            } else {
                this.interpreter.exec("gc.collect(0)");
            }
        } catch (Exception e) {
            log.warn("Jep interpreter cleanup failed", e);
        }
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
