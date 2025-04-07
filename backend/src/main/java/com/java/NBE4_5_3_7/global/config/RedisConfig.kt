package com.java.NBE4_5_3_7.global.config

import com.java.NBE4_5_3_7.domain.chat.model.Message
import com.java.NBE4_5_3_7.domain.chat.service.ChatPublisher
import com.java.NBE4_5_3_7.domain.chat.service.ChatSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.nio.charset.StandardCharsets

@Configuration
class RedisConfig(private val chatSubscriber: ChatSubscriber, @param:Lazy private val chatPublisher: ChatPublisher) {
    // RedisTemplate을 생성하여 Redis와 데이터를 송수신할 수 있도록 설정
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Message> {
        val template = RedisTemplate<String, Message>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }

    // Redis Pub/Sub을 구독하여 유저와 관리자 메시지를 처리하는 리스너 설정
    @Bean
    fun redisMessageListenerContainer(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.connectionFactory = connectionFactory

        // 유저 메시지를 구독하여 WebSocket을 통해 관리자에게 전달
        container.addMessageListener({ message: org.springframework.data.redis.connection.Message, pattern: ByteArray? ->
            val channel = String(message.channel, StandardCharsets.UTF_8)
            val messageContent = String(message.body, StandardCharsets.UTF_8)
            if (channel.startsWith("chat:")) {
                chatSubscriber.forwardUserMessageToRoom(messageContent, channel)
            } else if (channel.startsWith("admin:chat:")) {
                chatPublisher.publishToAdminChat(messageContent, channel)
            }
        }, PatternTopic("chat:*"))

        // 관리자 메시지를 구독하여 WebSocket을 통해 유저에게 전달
        container.addMessageListener({ message: org.springframework.data.redis.connection.Message, pattern: ByteArray? ->
            val channel = String(message.channel, StandardCharsets.UTF_8)
            val messageContent = String(message.body, StandardCharsets.UTF_8)
            println("✅ [RedisConfig] 관리자 메시지 수신 - 채널: $channel | 메시지: $messageContent")
            chatSubscriber.forwardAdminMessageToRoom(messageContent, channel) // ChatSubscriber가 직접 WebSocket으로 메시지 전송
        }, PatternTopic("admin:chat:*"))

        return container
    }
}