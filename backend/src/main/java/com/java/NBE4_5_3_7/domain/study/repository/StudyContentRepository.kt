package com.java.NBE4_5_3_7.domain.study.repository

import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface StudyContentRepository : JpaRepository<StudyContent?, Long?> {
    @Query("SELECT DISTINCT s.firstCategory FROM StudyContent s")
    fun findDistinctFirstCategories(): List<FirstCategory?>?

    @Query("SELECT DISTINCT s.secondCategory FROM StudyContent s WHERE s.firstCategory = :firstCategory")
    fun findDistinctBySecondCategory(firstCategory: FirstCategory?): List<String?>?

    fun findByFirstCategory(firstCategory: FirstCategory?, pageable: Pageable?): Page<StudyContent?>?

    fun findByFirstCategoryAndSecondCategory(
        firstCategory: FirstCategory?, secondCategory: String?, pageable: Pageable?
    ): Page<StudyContent?>?

    fun findByTitle(title: String?): Optional<StudyContent?>?
}
