package com.zm.excel.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadConfig {


    @Value("${threadpool.excel-manage.core-size}")
    private Integer corePoolSize;

    @Value("${threadpool.excel-manage.max-size}")
    private Integer maxPoolSize;

    @Value("${threadpool.excel-manage.queue-capacity}")
    private Integer queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor excelManageThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("excelManageThreadPool_");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }
}
