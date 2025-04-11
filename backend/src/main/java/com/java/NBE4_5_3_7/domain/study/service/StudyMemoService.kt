package com.java.NBE4_5_3_7.domain.study.service

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyMemoCreateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyMemoRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.response.StudyMemoResponseDto
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemo
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.java.NBE4_5_3_7.domain.study.repository.StudyMemoRepository
import com.java.NBE4_5_3_7.global.exception.ServiceException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StudyMemoService(
    private val studyMemoRepository: StudyMemoRepository,
    private val studyContentRepository: StudyContentRepository,
    private val studyMemoLikeService: StudyMemoLikeService,
) {
    // 멤버, 학습 컨텐츠 ID, 메모 내용 저장, 중복 작성 시 수정하게끔 변경
    @Transactional
    fun createStudyMemo(requestDto: StudyMemoCreateRequestDto, studyContentId: Long, member: Member) {
        val studyContent = studyContentRepository.findById(studyContentId)
            .orElseThrow { RuntimeException("존재하지 않는 학습 컨텐츠 입니다.") }!!

        var studyMemo = studyMemoRepository.findByMemberAndStudyContent(member, studyContent)

        if (studyMemo == null) {
            studyMemo = StudyMemo(requestDto.content, studyContent, member, requestDto.isPublished)
        } else {
            studyMemo.memoContent = requestDto.content
            studyMemo.isPublished = requestDto.isPublished
        }
        studyMemoRepository.save<StudyMemo>(studyMemo)
    }

    // 메모 단건 조회
    fun getStudyMemoByStudyMemberAndContentId(member: Member, studyContent: StudyContent?): StudyMemoResponseDto {
        val studyMemo = studyMemoRepository.findByMemberAndStudyContent(member, studyContent)
            ?: throw ServiceException("404", "해당 멤버의 학습 컨텐츠 메모가 존재하지 않습니다.")

        val likeCount = studyMemoLikeService.getLikeCount(studyMemo.id)
        return StudyMemoResponseDto(studyMemo, likeCount)
    }

    fun getStudyMemosByMemberAndCategory(member: Member, category: FirstCategory): List<StudyMemoResponseDto> {
        val memos = studyMemoRepository.findByMemberAndStudyContentCategory(member, category)

        return memos!!.stream()
            .map { memo: StudyMemo? ->
                StudyMemoResponseDto(
                    memo!!.id,
                    memo.memoContent,
                    memo.studyContent!!.study_content_id,
                    memo.studyContent!!.firstCategory!!.category,
                    memo.studyContent!!.title,
                    memo.studyContent!!.body,
                    studyMemoLikeService.getLikeCount(memo.id)
                )
            }
            .collect(Collectors.toList())
    }

    fun updateStudyMemo(studyMemoId: Long, updatedDto: StudyMemoRequestDto, member: Member): StudyMemoResponseDto {
        val studyMemo = studyMemoRepository.findById(studyMemoId)
            .orElseThrow { RuntimeException("해당 메모를 찾을 수 없습니다.") }!!

        if (studyMemo.member != member) {
            throw ServiceException("403", "본인이 작성한 메모만 수정할 수 있습니다.")
        }

        studyMemo.memoContent = updatedDto.memoContent
        val updatedMemo = studyMemoRepository.save(studyMemo)

        return StudyMemoResponseDto(
            updatedMemo.id,
            updatedMemo.memoContent,
            updatedMemo.studyContent!!.study_content_id,
            updatedMemo.studyContent!!.firstCategory!!.category,
            updatedMemo.studyContent!!.title,
            updatedMemo.studyContent!!.body,
            studyMemoLikeService.getLikeCount(updatedMemo.id)
        )
    }

    fun deleteStudyMemo(studyMemoId: Long, member: Member) {
        val studyMemo = studyMemoRepository.findById(studyMemoId)
            .orElseThrow { RuntimeException("해당 메모를 찾을 수 없습니다.") }!!

        if (studyMemo.member != member) {
            throw ServiceException("403", "본인이 작성한 댓글만 삭제할 수 있습니다.")
        }

        studyMemoRepository.delete(studyMemo)
    }

    fun  getStudyMemoListByStudyContentId(studyContentId: Long): List<StudyMemoResponseDto> {
        val studyContent = studyContentRepository.findById(studyContentId).orElse(null)
        val studyMemo = studyMemoRepository.findByStudyContent(studyContent)
        return studyMemo!!.stream()
            .map { memo: StudyMemo? -> StudyMemoResponseDto(memo!!, studyMemoLikeService.getLikeCount(memo.id)) }
            .toList()
    }
}
