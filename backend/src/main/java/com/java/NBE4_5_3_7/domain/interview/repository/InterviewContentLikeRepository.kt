package com.java.NBE4_5_3_7.domain.interview.repository

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentLike
import com.java.NBE4_5_3_7.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InterviewContentLikeRepository : JpaRepository<InterviewContentLike, Long> {
    fun findByInterviewContentAndMember(interviewContent: InterviewContent, member: Member): Optional<InterviewContentLike>

    fun countByInterviewContent(interviewContent: InterviewContent): Long
}