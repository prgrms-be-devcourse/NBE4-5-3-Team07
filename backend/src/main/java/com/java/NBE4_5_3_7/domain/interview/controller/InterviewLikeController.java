package com.java.NBE4_5_3_7.domain.interview.controller;

import com.java.NBE4_5_3_7.domain.interview.service.InterviewLikeService;
import com.java.NBE4_5_3_7.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview/like")
public class InterviewLikeController {

    private final InterviewLikeService likeService;
    private final MemberService memberService;

    public InterviewLikeController(InterviewLikeService likeService, MemberService memberService) {
        this.likeService = likeService;
        this.memberService = memberService;
    }

    @GetMapping("")
    public ResponseEntity<String> likeChange(@RequestParam("id") Long id) {
        return ResponseEntity.ok(likeService.interviewLike(memberService.getIdFromRq(), id));
    }

}
