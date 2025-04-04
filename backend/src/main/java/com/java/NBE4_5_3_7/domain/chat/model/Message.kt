package com.java.NBE4_5_3_7.domain.chat.model

data class Message(
    var roomId: Long?,
    var sender: String?, // ( ADMIN / USER / GUEST / SYSTEM )
    var content: String?,
    var timestamp: String?
)