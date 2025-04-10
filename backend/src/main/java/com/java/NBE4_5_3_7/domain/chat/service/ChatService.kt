package com.java.NBE4_5_3_7.domain.chat.service

import com.java.NBE4_5_3_7.domain.chat.entity.ChatMessageEntity
import com.java.NBE4_5_3_7.domain.chat.model.ChatRoom
import com.java.NBE4_5_3_7.domain.chat.model.Message
import com.java.NBE4_5_3_7.domain.chat.repository.ChatMessageRepository
import com.java.NBE4_5_3_7.domain.mail.EmailService
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.Rq
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

@Service
class ChatService(
    private val redisTemplate: RedisTemplate<String, Message>,
    private val emailService: EmailService,
    private val memberService: MemberService,
    private val chatMessageRepository: ChatMessageRepository,
    private val rq: Rq,
    private val applicationContext: ApplicationContext
) {
    private val self by lazy { applicationContext.getBean(ChatService::class.java) }

    /** 메시지 저장 */
    @Transactional
    fun saveMessage(roomId: Long, sender: String, content: String, timestamp: String) {
        val message = Message(roomId, sender, content, timestamp)
        redisTemplate.opsForList().rightPush("chat:$roomId", message)

        emailService.sendChatNotification(sender, content, timestamp)

        val redisChannel = if (sender == "ADMIN") "admin:chat:$roomId" else "chat:$roomId"
        redisTemplate.convertAndSend(redisChannel, message)
    }

    /** 채팅 내역 조회 */
    @Transactional(readOnly = true)
    fun getMessage(roomId: Long): List<Message> {
        val messages = redisTemplate
            .opsForList().range(
                "chat:$roomId", 0, -1
            )
        return messages ?: emptyList()
    }

    /** 전체 채팅 내역 조회 */
    @Transactional(readOnly = true)
    fun allMessages(): List<Message> {
        val keys = redisTemplate.keys("chat:*")
        val allMessages: MutableList<Message> =
            ArrayList()
        for (key in keys) {
            allMessages.addAll(
                Objects.requireNonNull(
                    redisTemplate.opsForList().range(key, 0, -1)
                )
            )
        }
        return allMessages
    }

    /** 채팅방 삭제 */
    @Transactional
    fun deleteChatRoomMessages(roomId: Long) {
        redisTemplate.delete("chat:$roomId")
    }

    /**  채팅방 목록 조회 */
    @Transactional(readOnly = true)
    fun chatRooms(): List<Long> {
        val keys = redisTemplate.keys("chat:*")
        return keys.stream()
            .map { key: String -> key.replace("chat:", "").toLong() }
            .collect(Collectors.toList())
    }

    /** 30분 이상 메시지 없는 게스트 채팅방 자동 삭제 */
    @Scheduled(cron = "0 0 * * * *")
    fun checkAndDeleteOldGuestRooms() {
        val now = LocalDateTime.now()
        val allRooms = self.chatRooms()

        for (roomId in allRooms) {
            if (roomId >= 0) continue

            val messages = self.getMessage(roomId)
            if (messages.isNotEmpty()) {
                val lastMessage = messages.last()
                try {
                    val instant = lastMessage.timestamp?.let { Instant.parse(it) }
                    val lastMessageTime = instant?.atZone(ZoneId.of("Asia/Seoul"))?.toLocalDateTime()
                    if (Duration.between(lastMessageTime, now).toMinutes() >= 30) {
                        self.deleteChatRoomMessages(roomId)
                    }
                } catch (e: Exception) {
                    System.err.println("⚠️ 타임스탬프 파싱 실패 - roomId=$roomId, timestamp=${lastMessage.timestamp}")
                }
            }
        }
    }

    /** 현재 사용자 정보 반환 ( ADMIN / USER / GUEST) */
    @Transactional(readOnly = true)
    fun chatRoomInfo(): ChatRoom {
        try {
            val actor: Member = rq.actor
            val realActor: Member = rq.getRealActor(actor)

            val isAdmin = memberService.isAdmin(realActor.id)
            if (isAdmin) {
                return ChatRoom(null, null, "ADMIN")
            }

            val roomId: Long? = realActor.id
            val nickname: String? = realActor.nickname
            return ChatRoom(roomId, nickname, "USER")
        } catch (e: java.lang.Exception) {
            val guestId = generateUniqueGuestId()
            return ChatRoom(guestId, "게스트 " + (-guestId), "GUEST")
        }
    }

    fun generateUniqueGuestId(): Long {
        val keys = redisTemplate.keys("chat:*")
        val usedGuestIds = keys.stream()
            .map { key: String ->
                try {
                    return@map key.replace("chat:", "").toLong()
                } catch (e: NumberFormatException) {
                    return@map 0L
                }
            }
            .filter { id: Long -> id < 0 }
            .collect(Collectors.toSet())

        var candidate: Long = -1
        while (usedGuestIds.contains(candidate)) {
            candidate--
        }
        return candidate
    }

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 실행
    @Transactional
    fun backupMessagesToMySQL() {
        val keys = redisTemplate.keys("chat:*")
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val messagesToSave = mutableListOf<ChatMessageEntity>()

        for (key in keys) {
            val roomId = key.removePrefix("chat:").toLongOrNull() ?: continue
            val messages = redisTemplate.opsForList().range(key, 0, -1) ?: continue

            for (message in messages) {
                try {
                    val time = message.timestamp?.let { LocalDateTime.parse(it, formatter) } ?: continue

                    // 중복 여부 확인
                    val isDuplicate = chatMessageRepository.existsByRoomIdAndSenderAndContentAndTimestamp(
                        roomId, message.sender, message.content, time
                    )

                    if (!isDuplicate) {
                        val entity = ChatMessageEntity(
                            roomId = roomId,
                            sender = message.sender,
                            content = message.content,
                            timestamp = time
                        )
                        messagesToSave.add(entity)
                    }
                } catch (e: Exception) {
                    System.err.println("⚠️ 백업 실패: roomId=$roomId, content=${message.content}, error=${e.message}")
                }
            }
        }

        chatMessageRepository.saveAll(messagesToSave)
        println("✅ 백업 완료: ${messagesToSave.size}건 (중복 제외)")
    }
}