package com.java.NBE4_5_3_7.global.config

import com.java.NBE4_5_3_7.domain.chat.model.Message
import com.java.NBE4_5_3_7.domain.chat.service.ChatPublisher
import com.java.NBE4_5_3_7.domain.chat.service.ChatSubscriber
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.nio.charset.StandardCharsets
import java.time.Duration


@Configuration
@EnableCaching
class RedisConfig(
    @param:Lazy private val chatSubscriber: ChatSubscriber,
    @param:Lazy private val chatPublisher: ChatPublisher) {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Message> {
        val template = RedisTemplate<String, Message>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }

    @Bean
    fun redisMessageListenerContainer(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.connectionFactory = connectionFactory

        container.addMessageListener({ message: org.springframework.data.redis.connection.Message, pattern: ByteArray? ->
            val channel = String(message.channel, StandardCharsets.UTF_8)
            val messageContent = String(message.body, StandardCharsets.UTF_8)
            if (channel.startsWith("chat:")) {
                chatSubscriber.forwardUserMessageToRoom(messageContent, channel)
            } else if (channel.startsWith("admin:chat:")) {
                chatPublisher.publishToAdminChat(messageContent, channel)
            }
        }, PatternTopic("chat:*"))

        container.addMessageListener({ message: org.springframework.data.redis.connection.Message, pattern: ByteArray? ->
            val channel = String(message.channel, StandardCharsets.UTF_8)
            val messageContent = String(message.body, StandardCharsets.UTF_8)
            chatSubscriber.forwardAdminMessageToRoom(messageContent, channel)
        }, PatternTopic("admin:chat:*"))

        return container
    }

    @Bean
    fun redisCacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10)) // TTL: 10분
            .disableCachingNullValues()
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build()
    }
}