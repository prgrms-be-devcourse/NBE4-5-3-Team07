package com.java.NBE4_5_3_7.domain.chat

import com.java.NBE4_5_3_7.domain.chat.service.ChatSubscriber
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.messaging.simp.SimpMessagingTemplate

class ChatSubscriberTest {
    private val messagingTemplate = mockk<SimpMessagingTemplate>(relaxed = true)
    private val chatSubscriber = ChatSubscriber(messagingTemplate)

    @Test
    fun `사용자 메시지 전송 TEST`() {
        val message = "User Hello"
        val channel = "chat:456"

        chatSubscriber.receiveMessage(message, channel)

        verify { messagingTemplate.convertAndSend("/topic/chat/456", message) }
    }

    @Test
    fun `관리자 메시지 전송 TEST`() {
        val message = "Admin Hello"
        val channel = "chat:admin:789"

        chatSubscriber.receiveAdminMessage(message, channel)

        verify { messagingTemplate.convertAndSend("/topic/admin/chat/789", message) }
    }
}