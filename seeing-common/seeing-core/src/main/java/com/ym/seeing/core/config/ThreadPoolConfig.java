package com.ym.seeing.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/20 10:40
 * @Desc: 线程池配置
 */
@Configuration
public class ThreadPoolConfig {
    public static final String TASK_EXECUTOR = "taskExecutor";
    public static final String TASK_SCHEDULER = "taskScheduler";

    /**
     * 异步任务执行
     * @return
     */
    @Bean
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setDaemon(true);
        taskExecutor.setThreadNamePrefix("seeing-async-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * 定时任务的执行
     * @return
     */
    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(100);
        taskScheduler.setDaemon(true);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setThreadNamePrefix("seeing-sche-");
        taskScheduler.initialize();
        return taskScheduler;
    }

}
