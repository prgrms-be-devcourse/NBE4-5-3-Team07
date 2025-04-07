package com.java.NBE4_5_3_7.domain.interview.repository

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark
import com.java.NBE4_5_3_7.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository : JpaRepository<InterviewContentBookmark, Long> {
    fun existsByMemberAndInterviewContent(member: Member, interviewContent: InterviewContent): Boolean
    fun findByMemberAndInterviewContent(member: Member, interviewContent: InterviewContent): InterviewContentBookmark
}