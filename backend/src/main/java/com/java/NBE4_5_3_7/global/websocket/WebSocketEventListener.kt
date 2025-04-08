package com.java.NBE4_5_3_7.global.websocket

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener {

    private val logger = LoggerFactory.getLogger(WebSocketEventListener::class.java)

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        logger.info("✅ WebSocket STOMP 연결 성공: ${event.user}")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        logger.info("⛔ WebSocket 연결 종료: ${event.sessionId}")
    }
}
