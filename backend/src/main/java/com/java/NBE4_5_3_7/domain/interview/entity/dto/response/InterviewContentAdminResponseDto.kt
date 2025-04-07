package com.java.NBE4_5_3_7.domain.interview.entity.dto.response

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent

data class InterviewContentAdminResponseDto(
    var id: Long? = null,
    var headId: Long? = null,
    var tailId: Long? = null,
    var isHead: Boolean = false,
    var hasTail: Boolean = false,
    var keyword: String? = null,
    var category: InterviewCategory? = null,
    var question: String? = null,
    var modelAnswer: String? = null,
    var likeCount: Long? = null
) {
    constructor(content: InterviewContent, likeCount: Long) : this(
        id = content.interviewContentId,
        headId = content.headId,
        tailId = content.tailId,
        isHead = content.isHead,
        hasTail = content.hasTail,
        keyword = content.keyword,
        category = content.category,
        question = content.question,
        modelAnswer = content.modelAnswer,
        likeCount = likeCount
    )
}