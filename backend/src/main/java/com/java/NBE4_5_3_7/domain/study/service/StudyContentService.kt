package com.java.NBE4_5_3_7.domain.study.service

import com.java.NBE4_5_3_7.domain.study.dto.response.StudyContentDetailDto
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudyContentService(private val studyContentRepository: StudyContentRepository) {
    fun findById(id: Long): StudyContent? {
        return studyContentRepository.findById(id)
            .orElse(null)
    }

    val allCategory: Map<String, List<String?>?>
        get() {
            val categories: MutableMap<String, List<String?>?> =
                HashMap()
            val firstCategories =
                studyContentRepository.findDistinctFirstCategories()
            for (category in firstCategories!!) {
                val firstCategory = category.toString()
                val second = getSecondCategoryByFirstCategory(firstCategory)
                categories[firstCategory.toString()] = second
            }
            return categories
        }

    val firstCategory: List<String>
        get() {
            val firstCategories =
                studyContentRepository.findDistinctFirstCategories()
            return firstCategories!!.stream()
                .map<String>(FirstCategory::category)
                .toList()
        }

    fun getSecondCategoryByFirstCategory(firstCategory: String): List<String?>? {
        val category = FirstCategory.valueOf(firstCategory)
        return studyContentRepository.findDistinctBySecondCategory(category)
    }

    // 다건 조회 (페이징 처리 추가)
    fun getStudyContentsByCategory(
        firstCategory: String,
        secondCategory: String?,
        pageable: Pageable?
    ): Page<StudyContentDetailDto> {
        val category = FirstCategory.valueOf(firstCategory)
        val studyContentsPage = studyContentRepository
            .findByFirstCategoryAndSecondCategory(category, secondCategory, pageable)

        return studyContentsPage!!.map { studyContent: StudyContent? ->
            StudyContentDetailDto(
                studyContent!!
            )
        }
    }

    @Transactional
    fun updateStudyContent(studyContentId: Long, updateContent: String?) {
        val studyContent = studyContentRepository.findById(studyContentId).orElse(null)
        studyContent!!.body = updateContent
    }

    fun deleteStudyContent(studyContentId: Long) {
        studyContentRepository.deleteById(studyContentId)
    }
}