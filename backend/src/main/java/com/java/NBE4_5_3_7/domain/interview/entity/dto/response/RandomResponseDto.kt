package com.java.NBE4_5_3_7.domain.interview.entity.dto.response

data class RandomResponseDto(
    var indexList: List<Long> = emptyList(),
    var interviewResponseDto: InterviewResponseDto? = null
)