package com.java.NBE4_5_3_7.domain.interviewComment.dto.response

data class InterviewCommentResponseDto(
    val commentId: Long? = null,
    val comment: String? = null,
    val isPublic: Boolean = false,
    val interviewContentId: Long? = null
)