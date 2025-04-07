package com.java.NBE4_5_3_7.domain.chat

import com.java.NBE4_5_3_7.domain.chat.model.Message
import com.java.NBE4_5_3_7.domain.chat.service.ChatService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestReporter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatServiceTest {

    @Autowired
    lateinit var chatService: ChatService

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Message>

    private val testRoomId = -999L

    @BeforeEach
    fun setUp() {
        redisTemplate.delete("chat:$testRoomId")
    }

    @Test
    fun `메세지 저장 TEST`(testReporter: TestReporter) {
        // given
        val sender = "USER"
        val content = "Hello!"
        val timestamp = Instant.now().toString()

        testReporter.publishEntry(mapOf(
            "sender" to sender,
            "content" to content,
            "timestamp" to timestamp
        ))

        // when
        chatService.saveMessage(testRoomId, sender, content, timestamp)

        // then
        val storedMessages = redisTemplate.opsForList().range("chat:$testRoomId", 0, -1)
        assertEquals(1, storedMessages?.size)
        assertEquals(content, storedMessages?.get(0)?.content)
    }

    @Test
    fun `메시지 조회 TEST`(testReporter: TestReporter) {
        // given
        val sender = "USER"
        val content = "조회 테스트 메시지"
        val timestamp = Instant.now().toString()
        redisTemplate.opsForList().rightPush("chat:$testRoomId", Message(testRoomId, sender, content, timestamp))

        // when
        val messages = chatService.getMessage(testRoomId)

        // then
        testReporter.publishEntry("messages size", messages.size.toString())
        assertEquals(1, messages.size)
        assertEquals(sender, messages[0].sender)
        assertEquals(content, messages[0].content)
    }

    @Test
    fun `채팅방 목록 조회 TEST`() {
        // given
        chatService.saveMessage(-999L, "USER", "msg1", Instant.now().toString())
        chatService.saveMessage(-998L, "USER", "msg2", Instant.now().toString())

        // when
        val chatRooms = chatService.chatRooms()

        // then
        assertTrue(chatRooms.contains(-999L))
        assertTrue(chatRooms.contains(-998L))
    }

    @Test
    fun `채팅방 삭제 TEST`() {
        // given
        chatService.saveMessage(testRoomId, "USER", "to delete", Instant.now().toString())
        assertTrue(chatService.getMessage(testRoomId).isNotEmpty())

        // when
        chatService.deleteChatRoomMessages(testRoomId)

        // then
        val result = chatService.getMessage(testRoomId)
        assertTrue(result.isEmpty())
    }
}