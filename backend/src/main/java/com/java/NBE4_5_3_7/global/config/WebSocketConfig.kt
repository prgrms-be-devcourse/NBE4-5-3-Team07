package com.java.NBE4_5_3_7.global.config

import com.java.NBE4_5_3_7.global.app.AppConfig.Companion.siteFrontUrl
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
	override fun registerStompEndpoints(registry: StompEndpointRegistry) {
		registry.addEndpoint("/ws/chat").setAllowedOrigins(siteFrontUrl).withSockJS()
	}

	override fun configureMessageBroker(registry: MessageBrokerRegistry) {
		registry.enableSimpleBroker("/topic")
		registry.setApplicationDestinationPrefixes("/app")
	}
}