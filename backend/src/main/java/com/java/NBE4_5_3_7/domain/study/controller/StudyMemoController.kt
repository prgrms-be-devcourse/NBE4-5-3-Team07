package com.java.NBE4_5_3_7.domain.study.controller

import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyMemoCreateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyMemoRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.response.StudyMemoResponseDto
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.service.StudyContentService
import com.java.NBE4_5_3_7.domain.study.service.StudyMemoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/studyMemo")
class StudyMemoController(
    private val studyMemoService: StudyMemoService,
    private val studyContentService: StudyContentService,
    private val memberService: MemberService
) {
    /** 메모 생성 */
    @PostMapping("/create/{studyContentId}")
    fun createStudyMemo(
        @RequestBody requestDto: StudyMemoCreateRequestDto,
        @PathVariable studyContentId: Long
    ): ResponseEntity<String> {
        println(requestDto.isPublished)
        studyMemoService.createStudyMemo(requestDto, studyContentId)
        return ResponseEntity.ok("create success")
    }

    /** 학습 컨텐츠에 대한 나의 메모 조회 */
    @GetMapping("/{studyContentId}")
    fun getStudyMemoByMemberAndStudyContentId(@PathVariable("studyContentId") studyContentId: Long): ResponseEntity<StudyMemoResponseDto> {
        val member = memberService.memberFromRq
        val studyContent = studyContentService.findById(studyContentId)
        return ResponseEntity.ok(studyMemoService.getStudyMemoByStudyMemberAndContentId(member, studyContent))
    }

    /** 학습 컨텐츠 별 사용자들의 공개 메모 리스트 반환 */
    @GetMapping("/list/{studyContentId}")
    fun getStudyMemoListByMemberAndStudyContentId(@PathVariable studyContentId: Long): ResponseEntity<List<StudyMemoResponseDto>> {
        return ResponseEntity.ok(studyMemoService.getStudyMemoListByStudyContentId(studyContentId))
    }

    /** 사용자 + 카테고리별 메모 및 컨텐츠 조회 */
    @GetMapping
    fun getStudyMemosByMemberAndCategory(@RequestParam category: String?): ResponseEntity<List<StudyMemoResponseDto>> {
        val member = memberService.memberFromRq

        val categoryEnum = FirstCategory.fromString(category!!)
        val studyMemos = studyMemoService.getStudyMemosByMemberAndCategory(member, categoryEnum)
        return ResponseEntity.ok(studyMemos)
    }

    /** 메모 수정 */
    @PatchMapping("/{studyMemoId}")
    fun updateStudyMemo(
        @PathVariable studyMemoId: Long,
        @RequestBody updatedDto: StudyMemoRequestDto
    ): ResponseEntity<StudyMemoResponseDto> {
        val member = memberService.memberFromRq

        val updatedStudyMemo = studyMemoService.updateStudyMemo(studyMemoId, updatedDto, member)
        return ResponseEntity.ok(updatedStudyMemo)
    }

    /** 메모 삭제 */
    @DeleteMapping("/{studyMemoId}")
    fun deleteStudyMemo(@PathVariable studyMemoId: Long): ResponseEntity<String> {
        val member = memberService.memberFromRq

        studyMemoService.deleteStudyMemo(studyMemoId, member)
        return ResponseEntity.ok("해당 메모가 삭제되었습니다.")
    }
}
