package com.java.NBE4_5_3_7.domain.interviewComment.controller

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interviewComment.dto.request.InterviewCommentRequestDto
import com.java.NBE4_5_3_7.domain.interviewComment.dto.response.InterviewCommentResponseDto
import com.java.NBE4_5_3_7.domain.interviewComment.dto.response.MyPageInterviewCommentResponseDto
import com.java.NBE4_5_3_7.domain.interviewComment.service.InterviewCommentService
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/interview/comment")
class InterviewCommentController(
    private val interviewCommentService: InterviewCommentService,
    private val memberService: MemberService
) {

    // 댓글 생성
    @PostMapping
    fun createComment(
        @RequestBody newDto: InterviewCommentRequestDto
    ): ResponseEntity<MyPageInterviewCommentResponseDto> {
        val member = memberService.getMemberFromRq()
        val createdComment = interviewCommentService.createComment(newDto, member)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment)
    }

    // 사용자 + 카테고리별 댓글 및 컨텐츠 조회
    @GetMapping
    fun getCommentsByMemberAndCategory(
        @RequestParam category: String
    ): ResponseEntity<List<MyPageInterviewCommentResponseDto>> {
        val member = memberService.getMemberFromRq()
        val categoryEnum = InterviewCategory.fromString(category)
        val comments = interviewCommentService.getCommentsByMemberAndCategory(member, categoryEnum)
        return ResponseEntity.ok(comments)
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody updatedDto: InterviewCommentRequestDto
    ): ResponseEntity<MyPageInterviewCommentResponseDto> {
        val member = memberService.getMemberFromRq()
        val updatedComment = interviewCommentService.updateComment(commentId, updatedDto, member)
        return ResponseEntity.ok(updatedComment)
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<String> {
        val member = memberService.getMemberFromRq()
        interviewCommentService.deleteComment(commentId, member)
        return ResponseEntity.ok("답변이 삭제되었습니다.")
    }

    // 내 메모 보기
    @GetMapping("/my/{interviewContentId}")
    fun getMyComments(
        @PathVariable interviewContentId: Long
    ): ResponseEntity<List<InterviewCommentResponseDto>> {
        val member = memberService.getMemberFromRq()
        val myComments = interviewCommentService.getMyComments(interviewContentId, member)
        return ResponseEntity.ok(myComments)
    }

    // 다른 사람 메모 보기
    @GetMapping("/public/{interviewContentId}")
    fun getPublicComments(
        @PathVariable interviewContentId: Long
    ): ResponseEntity<List<InterviewCommentResponseDto>> {
        val member = memberService.getMemberFromRq()
        val publicComments = interviewCommentService.getPublicComments(interviewContentId, member)
        return ResponseEntity.ok(publicComments)
    }
}