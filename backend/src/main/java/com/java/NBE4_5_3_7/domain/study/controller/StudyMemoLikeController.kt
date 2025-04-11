package com.java.NBE4_5_3_7.domain.study.controller

import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.domain.study.service.StudyMemoLikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/studyMemo/like")
class StudyMemoLikeController(
    private val studyMemoLikeService: StudyMemoLikeService,
    private val memberService: MemberService
) {
    @GetMapping("/{studyMemoId}/status")
    fun getLikeStatus(@PathVariable studyMemoId: Long): ResponseEntity<Map<String, Any>> {
        val member = memberService.getMemberFromRq()
        return ResponseEntity.ok(studyMemoLikeService.getLikeStatus(studyMemoId, member))
    }

    @PostMapping("/{studyMemoId}")
    fun saveStudyMemoLike(@PathVariable studyMemoId: Long): ResponseEntity<String> {
        val member = memberService.getMemberFromRq()
        return ResponseEntity.ok(studyMemoLikeService.memoLike(studyMemoId, member))
    }
}
