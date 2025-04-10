package com.java.NBE4_5_3_7.domain.interview.repository

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InterviewContentRepository : JpaRepository<InterviewContent, Long> {

    @Query("select ic.interviewContentId from InterviewContent ic where ic.isHead = true and ic.headId is null")
    fun findInterviewContentIdsByHeadTrueAndHeadIdIsNull(): List<Long>

    @Query("select ic.interviewContentId from InterviewContent ic where ic.category = :category")
    fun findInterviewContentIdsByCategory(
        @Param("category") category: InterviewCategory?
    ): List<Long>

    @Query(value = "select distinct keyword from interview_content", nativeQuery = true)
    fun findDistinctCategories(): List<String>

    @Query("select ic.interviewContentId from InterviewContent ic where ic.keyword in :keywords")
    fun findInterviewKeyword(@Param("keywords") keywords: List<String>): List<Long>

    @Query("SELECT c FROM InterviewContent c WHERE c.interviewContentId > :id ORDER BY c.interviewContentId ASC")
    fun findNextInterviewContent(@Param("id") id: Long, pageable: Pageable): List<InterviewContent>

    fun existsByQuestion(question: String): Boolean
}