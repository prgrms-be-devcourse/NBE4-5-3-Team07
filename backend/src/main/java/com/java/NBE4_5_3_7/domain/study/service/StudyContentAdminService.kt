package com.java.NBE4_5_3_7.domain.study.service

import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentCreateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentUpdateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.response.StudyContentDetailDto
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.java.NBE4_5_3_7.global.exception.ServiceException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudyContentAdminService(private val studyContentRepository: StudyContentRepository) {
    val allCategory: Map<String, List<String?>?>
        get() {
            val categories: MutableMap<String, List<String?>?> =
                HashMap()
            val firstCategories =
                studyContentRepository.findDistinctFirstCategories()

            if (firstCategories!!.isEmpty()) {
                throw ServiceException("404", "등록된 학습 콘텐츠 카테고리가 없습니다.")
            }

            for (category in firstCategories) {
                val firstCategory = category?.name
                val secondCategories =
                    studyContentRepository.findDistinctBySecondCategory(category)
                categories[firstCategory.toString()] = secondCategories
            }
            return categories
        }

    fun getPagedStudyContentsByCategory(firstCategory: String?, pageable: Pageable?): Page<StudyContentDetailDto> {
        val category: FirstCategory
        try {
            category = FirstCategory.fromString(firstCategory.toString())
        } catch (e: IllegalArgumentException) {
            throw ServiceException("400", "올바르지 않은 첫 번째 카테고리 값입니다: $firstCategory")
        }

        val studyContents = studyContentRepository.findByFirstCategory(category, pageable)

        if (studyContents!!.isEmpty) {
            throw ServiceException("404", "해당 카테고리에 학습 콘텐츠가 존재하지 않습니다.")
        }

        return studyContents.map { studyContent: StudyContent? ->
            StudyContentDetailDto(
                studyContent!!
            )
        }
    }

    fun getPagedStudyContentsByCategories(
        firstCategory: String?,
        secondCategory: String?,
        pageable: Pageable?
    ): Page<StudyContentDetailDto> {
        val category: FirstCategory
        try {
            category = FirstCategory.fromString(firstCategory.toString())
        } catch (e: IllegalArgumentException) {
            throw ServiceException("400", "올바르지 않은 첫 번째 카테고리 값입니다: $firstCategory")
        }

        val studyContents =
            studyContentRepository.findByFirstCategoryAndSecondCategory(category, secondCategory, pageable)

        if (studyContents!!.isEmpty) {
            throw ServiceException(
                "404",
                "해당 카테고리 조합에 학습 콘텐츠가 존재하지 않습니다.$firstCategory$secondCategory"
            )
        }

        return studyContents.map { studyContent: StudyContent? ->
            StudyContentDetailDto(
                studyContent!!
            )
        }
    }

    fun getStudyContentById(studyContentId: Long): StudyContentDetailDto {
        val studyContent = studyContentRepository.findById(studyContentId).orElseThrow {
            ServiceException(
                "404",
                "해당 학습 콘텐츠를 찾을 수 없습니다."
            )
        }!!
        return StudyContentDetailDto(studyContent)
    }

    @Transactional
    fun updateStudyContent(studyContentId: Long, requestDto: StudyContentUpdateRequestDto) {
        val studyContent = studyContentRepository.findById(studyContentId).orElseThrow {
            ServiceException(
                "404",
                "해당 학습 콘텐츠를 찾을 수 없습니다."
            )
        }!!

        if (requestDto.title != null && !requestDto.title.trim { it <= ' ' }.isEmpty()) {
            studyContent.title = requestDto.title
        }

        if (requestDto.firstCategory != null && !requestDto.firstCategory.trim { it <= ' ' }.isEmpty()) {
            try {
                val newCategory = FirstCategory.fromString(requestDto.firstCategory)

                val existingCategories = studyContentRepository.findDistinctFirstCategories()
                if (!existingCategories!!.contains(newCategory)) {
                    throw ServiceException("400", "존재하지 않는 첫 번째 카테고리입니다.")
                }

                studyContent.firstCategory = newCategory
            } catch (e: IllegalArgumentException) {
                throw ServiceException("400", "잘못된 첫 번째 카테고리 값입니다.")
            }
        }

        if (requestDto.secondCategory != null && !requestDto.secondCategory.trim { it <= ' ' }.isEmpty()) {
            studyContent.secondCategory = requestDto.secondCategory
        }

        if (requestDto.updateContent != null && !requestDto.updateContent.trim { it <= ' ' }.isEmpty()) {
            studyContent.body = requestDto.updateContent
        }
    }

    @Transactional
    fun deleteStudyContent(studyContentId: Long) {
        if (!studyContentRepository.existsById(studyContentId)) {
            throw ServiceException("404", "해당 학습 콘텐츠를 찾을 수 없습니다.")
        }
        studyContentRepository.deleteById(studyContentId)
    }

    @Transactional
    fun createStudyContent(requestDto: StudyContentCreateRequestDto): StudyContentDetailDto {
        val firstCategory: FirstCategory
        try {
            firstCategory = FirstCategory.fromString(requestDto.firstCategory.toString())
        } catch (e: IllegalArgumentException) {
            throw ServiceException("400", "존재하지 않는 첫 번째 카테고리입니다: " + requestDto.firstCategory.toString())
        }

        if (requestDto.secondCategory == null || requestDto.secondCategory!!.trim { it <= ' ' }
                .isEmpty() || requestDto.title == null || requestDto.title!!.trim { it <= ' ' }
                .isEmpty() || requestDto.body == null || requestDto.body!!.trim { it <= ' ' }.isEmpty()) {
            throw ServiceException("400", "두 번째 카테고리, 제목, 내용은 비워둘 수 없습니다.")
        }

        val studyContent = StudyContent()
        studyContent.firstCategory = firstCategory
        studyContent.secondCategory = requestDto.secondCategory
        studyContent.title = requestDto.title
        studyContent.body = requestDto.body

        studyContentRepository.save(studyContent)
        return StudyContentDetailDto(studyContent)
    }
}