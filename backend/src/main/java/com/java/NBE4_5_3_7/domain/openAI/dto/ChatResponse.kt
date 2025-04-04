package com.java.NBE4_5_3_7.domain.openAI.dto

import com.java.NBE4_5_3_7.domain.openAI.Message
import com.fasterxml.jackson.annotation.JsonProperty

class ChatResponse {
    @JsonProperty("choices")
    var choices: List<Choice>? = null

    class Choice {
        @JsonProperty("message")
        var message: Message? = null
    }
}