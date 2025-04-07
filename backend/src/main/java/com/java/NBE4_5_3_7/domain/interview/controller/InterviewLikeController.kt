package com.java.NBE4_5_3_7.domain.interview.controller

import com.java.NBE4_5_3_7.domain.interview.service.InterviewLikeService
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/interview/like")
class InterviewLikeController(
    private val likeService: InterviewLikeService,
    private val memberService: MemberService
) {

    @GetMapping("", produces = [MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"])
    fun likeChange(@RequestParam("id") id: Long): ResponseEntity<String> {
        val result = likeService.interviewLike(memberService.getIdFromRq(), id)
        return ResponseEntity.ok(result)
    }
}