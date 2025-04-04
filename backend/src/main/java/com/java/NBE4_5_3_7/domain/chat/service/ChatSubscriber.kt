package com.java.NBE4_5_3_7.domain.chat.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/** 사용자가 관리자에게 메시지를 보내는 로직 및 관리자 메시지 처리 */
@Service
class ChatSubscriber(
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun receiveMessage(message: String, channel: String) {
        val roomId = channel.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        messagingTemplate.convertAndSend("/topic/chat/$roomId", message)
    }

    fun receiveAdminMessage(message: String, channel: String) {
        val parts = channel.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val roomId = if (parts.size > 2) parts[2] else parts[1]

        messagingTemplate.convertAndSend("/topic/admin/chat/$roomId", message)
    }
}
