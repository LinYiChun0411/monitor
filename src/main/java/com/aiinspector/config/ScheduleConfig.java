package com.aiinspector.config;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ScheduleConfig {
	
	private ThreadPoolTaskScheduler scheduler;
   
	@Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("Scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        return scheduler;
    }
    
    @PreDestroy
	public void destroy() {
		if(null != scheduler) {
			scheduler.destroy();
		}
	}
}
