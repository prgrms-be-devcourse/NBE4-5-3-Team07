package com.java.NBE4_5_3_7.domain.interviewComment.dto.response

data class MyPageInterviewCommentResponseDto(
    val commentId: Long? = null,
    val comment: String? = null,
    val isPublic: Boolean = false,
    val interviewContentId: Long? = null,
    val interviewContentTitle: String? = null,
    val category: String? = null,
    val modelAnswer: String? = null
)