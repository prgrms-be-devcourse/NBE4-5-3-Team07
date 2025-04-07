package com.java.NBE4_5_3_7.domain.interview.controller

import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto
import com.java.NBE4_5_3_7.domain.interview.service.InterviewService
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/interview/bookmark")
class InterviewBookmarkController(
    private val interviewService: InterviewService,
    private val memberService: MemberService
) {

    @PostMapping(produces = [MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"])
    fun bookmarkAdd(@RequestParam("id") contentId: Long): ResponseEntity<String> {
        val memberId = memberService.getIdFromRq()
        return ResponseEntity.ok(interviewService.bookmark(memberId, contentId))
    }

    @GetMapping
    fun showBookmarkList(): ResponseEntity<List<BookmarkResponseDto>> {
        val memberId = memberService.getIdFromRq()
        return ResponseEntity.ok(interviewService.showMyBookmark(memberId))
    }

    @DeleteMapping("/{noteId}", produces = [MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"])
    fun deleteNote(@PathVariable noteId: Long): ResponseEntity<String> {
        val member: Member = memberService.getMemberFromRq()
        interviewService.deleteNote(noteId, member)
        return ResponseEntity.ok("북마크가 삭제되었습니다.")
    }
}