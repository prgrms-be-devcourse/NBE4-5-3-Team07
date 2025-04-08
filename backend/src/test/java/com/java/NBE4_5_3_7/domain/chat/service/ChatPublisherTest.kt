package com.java.NBE4_5_3_7.domain.chat.service

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.simp.SimpMessagingTemplate

@SpringBootTest
class ChatPublisherTest {

    private val messagingTemplate = mockk<SimpMessagingTemplate>(relaxed = true)
    private val chatPublisher = ChatPublisher(messagingTemplate)

    @Test
    fun `메시지 전송 경로 TEST`() {
        val message = "Hello Admin"
        val channel = "chat:admin:123"

        chatPublisher.publishToAdminChat(message, channel)

        verify { messagingTemplate.convertAndSend("/topic/admin/chat/123", message) }
    }
}