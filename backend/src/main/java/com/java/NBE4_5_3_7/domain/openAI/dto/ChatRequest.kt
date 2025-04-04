package com.java.NBE4_5_3_7.domain.openAI.dto

import com.java.NBE4_5_3_7.domain.openAI.Message
import com.fasterxml.jackson.annotation.JsonProperty

class ChatRequest {
    @JsonProperty("model")
    var model: String? = null

    @JsonProperty("messages")
    var messages: List<Message>? = null

    constructor()

    constructor(model: String?, messages: List<Message>?) {
        this.model = model
        this.messages = messages
    }
}