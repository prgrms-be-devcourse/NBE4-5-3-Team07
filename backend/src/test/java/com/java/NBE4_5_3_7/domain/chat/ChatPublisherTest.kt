package com.java.NBE4_5_3_7.domain.chat

import com.java.NBE4_5_3_7.domain.chat.service.ChatPublisher
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
    fun `ChatPublisher TEST`() {
        val message = "Hello Admin"
        val channel = "chat:admin:123"

        chatPublisher.receiveAdminMessage(message, channel)

        verify { messagingTemplate.convertAndSend("/topic/admin/chat/123", message) }
    }
}