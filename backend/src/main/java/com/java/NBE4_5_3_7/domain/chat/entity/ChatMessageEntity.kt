package com.java.NBE4_5_3_7.domain.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
class ChatMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "room_id")
    val roomId: Long,

    @Column(name = "sender")
    val sender: String,

    @Column(columnDefinition = "TEXT")
    val content: String,
    @Column(name = "timestamp")
    val timestamp: LocalDateTime
)
