package com.warranty.warranty.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AyscTaskExecutor {

    @Bean("asyncTaskExecutor")
    AsyncTaskExecutor asyncTaskExecutor()
	{
		ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(4);
		executor.setKeepAliveSeconds(100);
		executor.setQueueCapacity(150);
		executor.setMaxPoolSize(4);
		executor.setThreadNamePrefix("ThreadPoolTaskExecutor..");
		executor.initialize();
		return executor;
	}

}
