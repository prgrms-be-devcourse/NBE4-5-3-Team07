package com.java.NBE4_5_3_7.domain.chat.service

import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/** 사용자가 관리자에게 메시지를 보내는 로직 및 관리자 메시지 처리 */
@Service
class ChatSubscriber(
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val log = LoggerFactory.getLogger(ChatSubscriber::class.java)

    fun forwardUserMessageToRoom(message: String, channel: String) {
        val roomId = channel.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val path = "/topic/chat/$roomId"

        log.info("사용자 메시지 중계: [$path] - $message")

        messagingTemplate.convertAndSend(path, message)
    }

    fun forwardAdminMessageToRoom(message: String, channel: String) {
        val parts = channel.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val roomId = if (parts.size > 2) parts[2] else parts[1]
        val path = "/topic/admin/chat/$roomId"

        log.info("관리자 메시지 중계: [$path] - $message")

        messagingTemplate.convertAndSend(path, message)
    }
}
