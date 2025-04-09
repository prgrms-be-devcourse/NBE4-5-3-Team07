package com.java.NBE4_5_3_7.domain.study.repository

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StudyMemoRepository : JpaRepository<StudyMemo?, Long?> {
    @Query(
        "SELECT c FROM StudyMemo c JOIN c.studyContent ic " +
                "WHERE c.member = :member AND ic.firstCategory = :category"
    )
    fun findByMemberAndStudyContentCategory(
        @Param("member") member: Member,
        @Param("category") category: FirstCategory
    ): List<StudyMemo?>?

    @Query("SELECT s FROM StudyMemo s WHERE s.studyContent = :studyContent AND s.isPublished = true")
    fun findByStudyContent(studyContent: StudyContent?): List<StudyMemo?>?


    fun findByMemberAndStudyContent(member: Member?, studyContentId: StudyContent?): StudyMemo?
}
