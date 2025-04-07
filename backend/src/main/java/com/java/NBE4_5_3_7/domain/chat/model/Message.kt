package com.java.NBE4_5_3_7.domain.chat.model

data class Message(
    val roomId: Long = 0L,
    val sender: String = "", // ( ADMIN / USER / GUEST / SYSTEM )
    val content: String = "",
    val timestamp: String? = null
)