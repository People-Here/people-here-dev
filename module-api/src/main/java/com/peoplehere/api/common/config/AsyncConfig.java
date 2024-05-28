package com.peoplehere.api.common.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync(proxyTargetClass = true)
@Configuration
public class AsyncConfig {

	@Bean(name = "profile-translate")
	public Executor profileTranslateExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(20);
		executor.setThreadNamePrefix("profile-translate-");
		executor.initialize();
		return executor;
	}

	@Bean(name = "tour-translate")
	public Executor tourTranslateExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(30);
		executor.setThreadNamePrefix("tour-translate-");
		executor.initialize();
		return executor;
	}

	@Bean(name = "map-translate")
	public Executor mapTranslateExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(30);
		executor.setThreadNamePrefix("map-translate-");
		executor.initialize();
		return executor;
	}

}
