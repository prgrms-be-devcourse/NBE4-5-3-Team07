package com.java.NBE4_5_3_7.domain.chat.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/** 관리자가 메세지를 받는 로직 */
@Service
class ChatPublisher(
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun receiveAdminMessage(message: String, channel: String) {
        val parts = channel.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val roomId = if (parts.size > 2) parts[2] else parts[1]

        messagingTemplate.convertAndSend("/topic/admin/chat/$roomId", message)
    }
}