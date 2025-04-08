package com.java.NBE4_5_3_7.domain.chat.controller

import com.java.NBE4_5_3_7.domain.chat.model.ChatRoom
import com.java.NBE4_5_3_7.domain.chat.model.Message
import com.java.NBE4_5_3_7.domain.chat.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService)
{
    /** 채팅방 정보 조회 */
    @GetMapping("/chat/room/info")
    fun chatRoomInfo(): ResponseEntity<ChatRoom> {
        return ResponseEntity.ok(chatService.chatRoomInfo())
    }

    /** 유저 or 게스트 메시지 전송 */
    @MessageMapping("/chat/user/{roomId}")
    fun sendUserMessage(@DestinationVariable roomId: Long, message: Message) {
        chatService.saveMessage(roomId, message.sender!!, message.content!!, message.timestamp!!)
        messagingTemplate.convertAndSend("/topic/chat/$roomId", message)
    }

    /** 관리자 메시지 전송 */
    @MessageMapping("/chat/admin/{roomId}")
    fun sendAdminMessage(@DestinationVariable roomId: Long, message: Message) {
        chatService.saveMessage(roomId, "ADMIN", message.content!!, message.timestamp!!)
        messagingTemplate.convertAndSend("/topic/chat/$roomId", message)
    }

    /** 시스템 메시지 전송 */
    @MessageMapping("/chat/system/{roomId}")
    fun sendSystemMessage(@DestinationVariable roomId: Long, message: Message) {
        chatService.saveMessage(roomId, "SYSTEM", message.content!!, message.timestamp!!)
        messagingTemplate.convertAndSend("/topic/chat/$roomId", message)
    }

    /** 모든 메시지 조회 (관리자) */
    @GetMapping("/chat/messages/all")
    fun allMessages(): List<Message> {
        return chatService.allMessages()
    }

    /** 특정 채팅방 메시지 조회 */
    @GetMapping("/chat/messages/{roomId}")
    fun getMessage(@PathVariable roomId: Long): List<Message> {
        return chatService.getMessage(roomId)
    }

    /** 채팅방 목록 조회 (관리자) */
    @GetMapping("/chat/rooms")
    fun chatRooms(): List<Long> {
        return chatService.chatRooms()
    }

    /** 채팅방 삭제 */
    @DeleteMapping("/chat/messages/{roomId}")
    fun deleteChatRoomMessages(@PathVariable roomId: Long) {
        chatService.deleteChatRoomMessages(roomId)
    }
}