package com.java.NBE4_5_3_7.domain.openAI

import com.fasterxml.jackson.annotation.JsonProperty

class Message {
    @JsonProperty("role")
    var role: String? = null // "system", "user", "assistant"

    @JsonProperty("content")
    var content: String? = null

    // 기본 생성자
    constructor()

    // 전체 필드 생성자
    constructor(role: String?, content: String?) {
        this.role = role
        this.content = content
    }
}