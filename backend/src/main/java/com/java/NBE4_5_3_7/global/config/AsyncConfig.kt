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
		executor.corePoolSize = 5 // 코어 스레드 개수 (기본적으로 항상 실행됨)
		executor.maxPoolSize = 10 // 최대 스레드 개수
		executor.queueCapacity = 50 // 작업 큐 크기 (대기 중인 작업 수)
		executor.threadNamePrefix = "AsyncThread-" // 스레드 이름 설정
		executor.initialize()
		return executor
	}
}