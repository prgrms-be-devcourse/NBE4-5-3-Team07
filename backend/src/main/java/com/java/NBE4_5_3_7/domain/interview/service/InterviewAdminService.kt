package com.java.NBE4_5_3_7.domain.interview.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewContentAdminResponseDto
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentAdminRepository
import com.java.NBE4_5_3_7.global.exception.ServiceException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class InterviewAdminService(private val interviewContentAdminRepository: InterviewContentAdminRepository) {
    val categoryKeywords: Map<String, List<String>>
        // 카테고리별 키워드 목록 조회
        get() {
            val categoryKeywords: MutableMap<String, List<String>> =
                HashMap()
            val categories =
                interviewContentAdminRepository.findUniqueCategories()

            if (categories.isEmpty()) {
                throw ServiceException("404", "등록된 면접 질문 카테고리가 없습니다.")
            }

            for (category in categories) {
                val categoryName = category.name
                val keywords =
                    interviewContentAdminRepository.findUniqueKeywordsByCategory(category)
                categoryKeywords[categoryName] = keywords
            }

            return categoryKeywords
        }

    // 특정 카테고리의 모든 면접 질문 조회
    fun getInterviewsByCategory(
        category: InterviewCategory?,
        page: Int,
        size: Int
    ): Page<InterviewContentAdminResponseDto> {
        val pageable: Pageable = PageRequest.of(page, size)
        val contents = interviewContentAdminRepository.findByCategory(category, pageable)

        if (contents.isEmpty) {
            throw ServiceException("404", "해당 카테고리에 속하는 면접 질문이 없습니다.")
        }

        return contents.map { content: InterviewContent ->
            val likeCount =
                interviewContentAdminRepository.countLikesByInterviewContentId(content.interviewContentId)
            InterviewContentAdminResponseDto(content, likeCount)
        }
    }

    // 특정 카테고리와 키워드를 포함하는 면접 질문 조회
    fun getInterviewsByCategoryAndKeyword(
        category: InterviewCategory?,
        keyword: String?,
        page: Int,
        size: Int
    ): Page<InterviewContentAdminResponseDto> {
        val pageable: Pageable = PageRequest.of(page, size)
        val contents = interviewContentAdminRepository.findByCategoryAndKeyword(category, keyword, pageable)

        if (contents.isEmpty) {
            throw ServiceException("404", "해당 카테고리와 키워드를 포함하는 면접 질문이 없습니다.")
        }

        return contents.map { content: InterviewContent ->
            val likeCount =
                interviewContentAdminRepository.countLikesByInterviewContentId(content.interviewContentId)
            InterviewContentAdminResponseDto(content, likeCount)
        }
    }

    // 특정 면접 질문 ID 조회
    fun getInterviewContentById(interviewContentId: Long): InterviewContentAdminResponseDto {
        val content = interviewContentAdminRepository.findById(interviewContentId).orElseThrow {
            ServiceException(
                "404",
                "해당 ID의 면접 질문을 찾을 수 없습니다."
            )
        }
        val likeCount = interviewContentAdminRepository.countLikesByInterviewContentId(content.interviewContentId)
        return InterviewContentAdminResponseDto(content, likeCount)
    }

    // 주어진 interviewContentId에 해당하는 면접 질문을 조회하고, 관련된 모든 꼬리 질문을 포함하여 반환
    @Transactional
    fun getInterviewContentWithAllTails(interviewContentId: Long): List<InterviewContentAdminResponseDto> {
        val content = interviewContentAdminRepository.findById(interviewContentId).orElseThrow {
            ServiceException(
                "404",
                "해당 ID의 면접 질문을 찾을 수 없습니다."
            )
        }
        val likeCount = interviewContentAdminRepository.countLikesByInterviewContentId(content.interviewContentId)

        val relatedQuestions = getTailQuestions(content.interviewContentId!!)

        val result: MutableList<InterviewContentAdminResponseDto> = ArrayList()
        result.add(InterviewContentAdminResponseDto(content, likeCount))
        result.addAll(relatedQuestions)
        return result
    }

    // 주어진 headId를 기준으로 연관된 꼬리 질문들을 조회
    private fun getTailQuestions(headId: Long): List<InterviewContentAdminResponseDto> {
        val tails = interviewContentAdminRepository.findRelatedQuestions(headId)

        val result: MutableList<InterviewContentAdminResponseDto> = ArrayList()
        for (tail in tails) {
            val likeCount = interviewContentAdminRepository.countLikesByInterviewContentId(tail.interviewContentId)
            result.add(InterviewContentAdminResponseDto(tail, likeCount))

            if (tail.hasTail) {
                result.addAll(getTailQuestions(tail.interviewContentId!!))
            }
        }
        return result
    }

    // 면접 질문 ID를 기준으로 카테고리, 키워드, 질문, 모범 답안을 수정
    @Transactional
    fun updateInterviewContent(
        interviewContentId: Long,
        requestDto: InterviewContentAdminRequestDto
    ): InterviewContentAdminResponseDto {
        val content = interviewContentAdminRepository.findById(interviewContentId).orElseThrow {
            ServiceException(
                "404",
                "해당 ID의 면접 질문을 찾을 수 없습니다."
            )
        }

        content.category = requestDto.category!!
        content.keyword = requestDto.keyword
        content.question = requestDto.question!!
        content.modelAnswer = requestDto.modelAnswer!!

        interviewContentAdminRepository.save(content)

        val likeCount = interviewContentAdminRepository.countLikesByInterviewContentId(content.interviewContentId)
        return InterviewContentAdminResponseDto(content, likeCount)
    }

    // 특정 면접 질문과 모든 꼬리 질문을 삭제
    @Transactional
    fun deleteInterviewContentWithAllTails(interviewContentId: Long) {
        val content = interviewContentAdminRepository.findById(interviewContentId).orElseThrow {
            ServiceException(
                "404",
                "해당 ID의 면접 질문을 찾을 수 없습니다."
            )
        }

        val headId = content.headId
        val relatedQuestions = getTailContents(content.interviewContentId!!)

        if (headId != null) {
            val headQuestion = interviewContentAdminRepository.findById(headId).orElse(null)
            if (headQuestion != null) {
                headQuestion.removeTail()
                interviewContentAdminRepository.save(headQuestion)
            }
        }

        interviewContentAdminRepository.deleteAll(relatedQuestions)
        interviewContentAdminRepository.delete(content)
    }

    // 주어진 headId를 기준으로 연관된 모든 꼬리 질문들을 조회
    private fun getTailContents(headId: Long): List<InterviewContent> {
        val tails = interviewContentAdminRepository.findRelatedQuestions(headId)
        val result: MutableList<InterviewContent> = ArrayList(tails)

        for (tail in tails) {
            if (tail.hasTail) {
                result.addAll(getTailContents(tail.interviewContentId!!))
            }
        }
        return result
    }

    @Transactional
    fun createInterviewContent(requestDto: InterviewContentAdminRequestDto): InterviewContentAdminResponseDto {
        // 머리 질문인지 확인
        if (requestDto.headId == null) {
            val newHeadContent = InterviewContent.createNewHead(
                requestDto.question!!,
                requestDto.modelAnswer!!,
                requestDto.category!!,
                requestDto.keyword!!
            )

            interviewContentAdminRepository.save(newHeadContent)
            return InterviewContentAdminResponseDto(newHeadContent, 0L)
        }

        // 꼬리 질문을 붙힐 머리 질문 정보 가져오기
        val headContent = interviewContentAdminRepository.findById(requestDto.headId).orElseThrow {
            ServiceException(
                "404",
                "해당 ID의 면접 질문을 찾을 수 없습니다."
            )
        }

        // 머리 질문이 중간 질문인지 확인 (중간 질문에는 추가 불가능)
        if (headContent.hasTail) {
            throw ServiceException("400", "중간 질문에는 꼬리 질문을 추가할 수 없습니다. 마지막 질문에만 추가할 수 있습니다.")
        }

        // 질문 생성
        val newTailContent = InterviewContent.createTail(headContent, requestDto)
        interviewContentAdminRepository.save(headContent)
        interviewContentAdminRepository.save(newTailContent)

        return InterviewContentAdminResponseDto(newTailContent, 0L)
    }
}