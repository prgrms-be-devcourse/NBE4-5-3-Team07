package com.java.NBE4_5_3_7.domain.chat.repository

import com.java.NBE4_5_3_7.domain.chat.entity.ChatMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, Long> {
    fun existsByRoomIdAndSenderAndContentAndTimestamp(
        roomId: Long,
        sender: String,
        content: String,
        timestamp: LocalDateTime
    ): Boolean

}

