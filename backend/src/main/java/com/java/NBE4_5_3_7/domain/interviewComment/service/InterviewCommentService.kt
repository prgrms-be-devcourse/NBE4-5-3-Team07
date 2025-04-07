package com.java.NBE4_5_3_7.domain.interviewComment.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.java.NBE4_5_3_7.domain.interviewComment.dto.request.InterviewCommentRequestDto
import com.java.NBE4_5_3_7.domain.interviewComment.dto.response.InterviewCommentResponseDto
import com.java.NBE4_5_3_7.domain.interviewComment.dto.response.MyPageInterviewCommentResponseDto
import com.java.NBE4_5_3_7.domain.interviewComment.entity.InterviewContentComment
import com.java.NBE4_5_3_7.domain.interviewComment.repository.InterviewCommentRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.global.exception.ServiceException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class InterviewCommentService(
    private val interviewCommentRepository: InterviewCommentRepository,
    private val interviewContentRepository: InterviewContentRepository
) {

    @Transactional
    fun createComment(newDto: InterviewCommentRequestDto, member: Member): MyPageInterviewCommentResponseDto {
        val interviewContent = interviewContentRepository.findById(newDto.interviewContentId)
            .orElseThrow { ServiceException("404", "해당 인터뷰 콘텐츠를 찾을 수 없습니다.") }

        val newComment = InterviewContentComment(
            answer = newDto.comment,
            isPublic = newDto.isPublic,
            interviewContent = interviewContent,
            member = member
        )

        val savedComment = interviewCommentRepository.save(newComment)

        return MyPageInterviewCommentResponseDto(
            savedComment.commentId!!,
            savedComment.answer,
            savedComment.isPublic,
            savedComment.interviewContent!!.interviewContentId,
            savedComment.interviewContent!!.question,
            savedComment.interviewContent!!.category.category,
            savedComment.interviewContent!!.modelAnswer
        )
    }

    fun getCommentsByMemberAndCategory(member: Member, category: InterviewCategory): List<MyPageInterviewCommentResponseDto> {
        val comments = interviewCommentRepository.findByMemberAndInterviewContentCategory(member, category)

        return comments.map {
            MyPageInterviewCommentResponseDto(
                it.commentId!!,
                it.answer,
                it.isPublic,
                it.interviewContent!!.interviewContentId,
                it.interviewContent!!.question,
                it.interviewContent!!.category.category,
                it.interviewContent!!.modelAnswer
            )
        }
    }

    @Transactional
    fun updateComment(commentId: Long, updatedDto: InterviewCommentRequestDto, member: Member): MyPageInterviewCommentResponseDto {
        val comment = interviewCommentRepository.findById(commentId)
            .orElseThrow { ServiceException("404", "해당 댓글을 찾을 수 없습니다.") }

        if (comment.member != member) {
            throw ServiceException("403", "본인이 작성한 댓글만 수정할 수 있습니다.")
        }

        comment.answer = updatedDto.comment
        comment.isPublic = updatedDto.isPublic

        return MyPageInterviewCommentResponseDto(
            comment.commentId!!,
            comment.answer,
            comment.isPublic,
            comment.interviewContent!!.interviewContentId,
            comment.interviewContent!!.question,
            comment.interviewContent!!.category.category,
            comment.interviewContent!!.modelAnswer
        )
    }

    @Transactional
    fun deleteComment(commentId: Long, member: Member) {
        val comment = interviewCommentRepository.findById(commentId)
            .orElseThrow { ServiceException("404", "댓글을 찾을 수 없습니다.") }

        if (comment.member != member) {
            throw ServiceException("403", "본인이 작성한 댓글만 삭제할 수 있습니다.")
        }

        interviewCommentRepository.deleteById(commentId)
    }

    @Transactional
    fun getMyComments(interviewContentId: Long, member: Member): List<InterviewCommentResponseDto> {
        return interviewCommentRepository.findByInterviewContentId(interviewContentId)
            .filter { it.member == member }
            .map {
                InterviewCommentResponseDto(
                    it.commentId!!,
                    it.answer,
                    it.isPublic,
                    it.interviewContent!!.interviewContentId
                )
            }
    }

    @Transactional
    fun getPublicComments(interviewContentId: Long, member: Member): List<InterviewCommentResponseDto> {
        return interviewCommentRepository.findByInterviewContentId(interviewContentId)
            .filter { it.member != member && it.isPublic }
            .map {
                InterviewCommentResponseDto(
                    it.commentId!!,
                    it.answer,
                    it.isPublic,
                    it.interviewContent!!.interviewContentId
                )
            }
    }
}