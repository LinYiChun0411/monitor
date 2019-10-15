package com.aiinspector.config;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {
	private ThreadPoolTaskExecutor executor;
	@Bean
	 public ThreadPoolTaskExecutor threadPool(){
		 executor = new ThreadPoolTaskExecutor();  
		 executor.setCorePoolSize(10);
		 executor.setMaxPoolSize(300);
		 executor.setQueueCapacity(5000);
         executor.setAllowCoreThreadTimeOut(true);
         executor.setThreadNamePrefix("ThreadPool-");
         executor.setKeepAliveSeconds(300);
         executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());  
         return executor;
     }
	@PreDestroy
	public void destroy() {
		if(null != executor) {
			executor.shutdown();
		}
	}
}
