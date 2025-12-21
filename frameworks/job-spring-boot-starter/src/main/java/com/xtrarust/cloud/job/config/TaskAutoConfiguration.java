package com.xtrarust.cloud.job.config;

import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务 Configuration<br>
 *
 * 注意：<br>
 * Spring定时任务默认是单线程的参见ScheduledTaskRegistrar#scheduledTasks方法<br>
 * Spring Boot中TaskSchedulingAutoConfiguration则会默认创建单线程ThreadPoolTaskScheduler<br>
 *
 * @see org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
 * @see org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
 */
@Slf4j
@EnableAsync(proxyTargetClass = true)
@AutoConfiguration(before = {TaskExecutionAutoConfiguration.class})
@ConditionalOnClass(ThreadPoolTaskExecutor.class)
public class AsyncAutoConfiguration {

    private static final int DEFAULT_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    // ----------------------------------- 异步任务 -----------------------------------
    @Bean
    @ConditionalOnThreading(Threading.PLATFORM)
    public TaskDecorator taskDecorator() {
        // 修改提交的任务，接入 TransmittableThreadLocal
        log.info("异步任务接入 TransmittableThreadLocal");
        return TtlRunnable::get;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    public ThreadPoolTaskExecutorCustomizer threadPoolTaskExecutorCustomizer() {
        return taskExecutor -> {
            // 自定义拒绝策略 默认使用的是AbortPolicy
            taskExecutor.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());
        };
    }

    static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.info("reject execute task: {}", r);
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
        }
    }

    // ----------------------------------- 定时任务 -----------------------------------
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    public ThreadPoolTaskSchedulerCustomizer threadPoolTaskSchedulerCustomizer() {
        return taskScheduler -> {
            if (taskScheduler.getPoolSize() == 1) {
                log.info("定时任务线程数调整: 1 -> {}", DEFAULT_POOL_SIZE);
                taskScheduler.setPoolSize(DEFAULT_POOL_SIZE);
            }
            taskScheduler.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());
        };
    }
}
