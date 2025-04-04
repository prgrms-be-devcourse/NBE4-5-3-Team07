package com.java.NBE4_5_3_7.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class SchedulerConfig {
	@Bean
	@Primary
	fun taskScheduler(): TaskScheduler {
		val scheduler = ThreadPoolTaskScheduler()
		scheduler.poolSize = 5
		scheduler.threadNamePrefix = "SchedulerThread-"
		scheduler.initialize()
		return scheduler
	}
}