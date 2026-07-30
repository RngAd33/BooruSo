package com.rngad33.booruso.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 * 为网络请求提供专用线程池，避免阻塞主线程
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    /** 核心线程数 */
    private static final int CORE_POOL_SIZE = 4;
    /** 最大线程数 */
    private static final int MAX_POOL_SIZE = 16;
    /** 任务队列容量 */
    private static final int QUEUE_CAPACITY = 100;
    /** 线程空闲存活时间（秒） */
    private static final int KEEP_ALIVE_SECONDS = 60;
    /** 线程名前缀 */
    private static final String THREAD_NAME_PREFIX = "async-http-";

    @Bean(name = "httpTaskExecutor")
    public Executor httpTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 队列满时的拒绝策略：由调用线程执行（降级为同步）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 优雅关闭：等待现有任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return httpTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                log.error("异步任务执行异常 - 方法: {}, 参数: {}, 错误: {}",
                        method.getName(), params, ex.getMessage());
    }
}
