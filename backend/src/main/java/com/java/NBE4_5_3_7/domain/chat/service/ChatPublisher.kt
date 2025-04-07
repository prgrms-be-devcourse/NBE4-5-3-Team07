package com.java.NBE4_5_3_7.domain.chat.service

import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/** 관리자가 메세지를 받는 로직 */
@Service
class ChatPublisher(
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val log = LoggerFactory.getLogger(ChatSubscriber::class.java)

    fun publishToAdminChat(message: String, channel: String) {
        val parts = channel.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val roomId = if (parts.size > 2) parts[2] else parts[1]
        val path = "/topic/admin/chat/$roomId"

        log.info("관리자 채팅방으로 메시지 발송 -> [$path] - $message")

        messagingTemplate.convertAndSend(path, message)
    }
}