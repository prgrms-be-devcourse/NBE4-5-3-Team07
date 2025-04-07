package com.java.NBE4_5_3_7.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {
	@Bean(name = ["asyncExecutor"])
	fun taskExecutor(): Executor {
		val executor = ThreadPoolTaskExecutor()
		executor.corePoolSize = 5
		executor.maxPoolSize = 10
		executor.queueCapacity = 50
		executor.threadNamePrefix = "AsyncThread-"
		executor.initialize()
		return executor
	}
}