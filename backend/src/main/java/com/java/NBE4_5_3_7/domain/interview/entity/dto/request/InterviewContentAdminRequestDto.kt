package com.java.NBE4_5_3_7.domain.interview.entity.dto.request

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory

data class InterviewContentAdminRequestDto(
    var headId: Long? = null,
    var tailId: Long? = null,
    var isHead: Boolean = false,
    var hasTail: Boolean = false,
    var keyword: String? = null,
    var category: InterviewCategory? = null,
    var question: String? = null,
    var modelAnswer: String? = null
) {
    constructor(
        interviewCategory: InterviewCategory,
        newKeyword: String,
        newQ: String,
        newA: String,
        o: Any? = null
    ) : this(
        category = interviewCategory,
        keyword = newKeyword,
        question = newQ,
        modelAnswer = newA
    )
}