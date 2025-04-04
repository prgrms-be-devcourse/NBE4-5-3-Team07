package com.java.NBE4_5_3_7.global.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig {
    @Value("\${redisson.address}")
    private val redisAddress: String? = null

    @Bean(destroyMethod = "shutdown")
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress(redisAddress)
        // 필요한 경우 비밀번호, 커넥션 풀 등 추가 설정
        return Redisson.create(config)
    }
}