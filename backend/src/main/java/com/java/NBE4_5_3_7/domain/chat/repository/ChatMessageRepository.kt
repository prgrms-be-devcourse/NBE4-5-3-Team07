package com.java.NBE4_5_3_7.domain.chat.repository

import com.java.NBE4_5_3_7.domain.chat.entity.ChatMessageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, Long>
