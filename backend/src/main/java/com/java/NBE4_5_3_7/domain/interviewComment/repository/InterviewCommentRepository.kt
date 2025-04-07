package com.java.NBE4_5_3_7.domain.interviewComment.repository

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interviewComment.entity.InterviewContentComment
import com.java.NBE4_5_3_7.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InterviewCommentRepository : JpaRepository<InterviewContentComment, Long> {

    @Query(
        """
        SELECT c FROM InterviewContentComment c 
        JOIN c.interviewContent ic 
        WHERE c.member = :member AND ic.category = :category
        """
    )
    fun findByMemberAndInterviewContentCategory(
        @Param("member") member: Member,
        @Param("category") category: InterviewCategory
    ): List<InterviewContentComment>

    @Query(
        "SELECT c FROM InterviewContentComment c WHERE c.interviewContent.interviewContentId = :interviewContentId"
    )
    fun findByInterviewContentId(
        @Param("interviewContentId") interviewContentId: Long
    ): List<InterviewContentComment>
}