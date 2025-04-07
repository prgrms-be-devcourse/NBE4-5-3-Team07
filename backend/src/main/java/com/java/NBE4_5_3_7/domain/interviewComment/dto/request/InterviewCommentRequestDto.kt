package com.java.NBE4_5_3_7.domain.interviewComment.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class InterviewCommentRequestDto(
    val comment: String? = null,

    @JsonProperty("isPublic")
    val isPublic: Boolean = false,

    val interviewContentId: Long = 0L
)