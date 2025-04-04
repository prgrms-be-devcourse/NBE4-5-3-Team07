package com.java.NBE4_5_3_7.domain.chat

import com.java.NBE4_5_3_7.domain.chat.service.ChatService
import com.java.NBE4_5_3_7.domain.chat.model.Message
import org.junit.jupiter.api.*
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
    fun `메시지를 저장하고 조회할 수 있다`() {
        // given
        val sender = "testUser"
        val content = "Hello, World!"
        val timestamp = Instant.now().toString()

        // when
        chatService.saveMessage(testRoomId, sender, content, timestamp)
        val messages = chatService.getMessage(testRoomId)

        // then
        assertEquals(1, messages.size)
        assertEquals(sender, messages[0].sender)
        assertEquals(content, messages[0].content)
        assertEquals(timestamp, messages[0].timestamp)
    }

    @Test
    fun `채팅방 메시지를 삭제하면 메시지가 비워진다`() {
        // given
        chatService.saveMessage(testRoomId, "sender", "to delete", Instant.now().toString())
        assertTrue(chatService.getMessage(testRoomId).isNotEmpty())

        // when
        chatService.deleteChatRoomMessages(testRoomId)

        // then
        val result = chatService.getMessage(testRoomId)
        assertTrue(result.isEmpty())
    }
}