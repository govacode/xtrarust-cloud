package com.xtrarust.cloud.common.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * 线程相关工具类
 *
 * @author ruoyi
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Threads {

    /**
     * 停止线程池
     * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
     * 如果超时, 则调用shutdownNow, 取消在workQueue中Pending的任务,并中断所有阻塞函数.
     * 如果仍然超時，則強制退出.
     * 另对在shutdown时线程本身被调用中断做了处理.
     */
    public static void shutdownAndAwaitTermination(ExecutorService threadPool, Duration timeout) {
        timeout = ObjectUtil.defaultIfNull(timeout, Duration.ofMinutes(2));
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(timeout.getSeconds(), TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                    if (!threadPool.awaitTermination(timeout.getSeconds(), TimeUnit.SECONDS)) {
                        log.info("thread pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 打印线程异常信息
     */
    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            log.error(t.getMessage(), t);
        }
    }
}
