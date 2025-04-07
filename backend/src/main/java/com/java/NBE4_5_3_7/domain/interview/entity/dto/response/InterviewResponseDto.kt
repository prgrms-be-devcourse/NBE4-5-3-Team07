package com.java.NBE4_5_3_7.domain.interview.entity.dto.response

data class InterviewResponseDto(
    var id: Long? = null,
    var head_id: Long? = null,
    var tail_id: Long? = null,
    var question: String? = null,
    var model_answer: String? = null,
    var category: String? = null,
    var keyword: String? = null,
    var next_id: Long? = null,
    var likeCount: Long? = null,
    var likedByUser: Boolean = false
)