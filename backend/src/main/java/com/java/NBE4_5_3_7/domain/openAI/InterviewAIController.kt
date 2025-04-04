package com.java.NBE4_5_3_7.domain.openAI

import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.domain.openAI.dto.InterviewEvaluationDto
import com.java.NBE4_5_3_7.domain.openAI.dto.InterviewNextDto
import com.java.NBE4_5_3_7.domain.openAI.dto.InterviewStartDto
import com.java.NBE4_5_3_7.global.customAnnotation.PremiumAccess
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/interview")
class InterviewAIController(
    private val openAiService: OpenAiService,
    private val memberService: MemberService
) {
    /**
     * 인터뷰 시작
     */
    @PremiumAccess
    @PostMapping("/start")
    fun startInterview(@RequestBody dto: InterviewStartDto): ResponseEntity<String> {
        val response = openAiService.askStartInterview(dto.interviewType)
        return ResponseEntity.ok(response)
    }

    /**
     * 인터뷰 진행 (후속 질문)
     */
    @PremiumAccess
    @PostMapping("/next")
    fun nextInterview(@RequestBody dto: InterviewNextDto): ResponseEntity<String> {
        val response = openAiService.askNextInterview(dto.interviewType, dto.answer)
        return ResponseEntity.ok(response)
    }

    /**
     * 인터뷰 평가
     */
    @PremiumAccess
    @PostMapping("/evaluation")
    fun evaluateInterview(@RequestBody dto: InterviewEvaluationDto): ResponseEntity<String> {
        val evaluation = openAiService.evaluateInterview(dto.conversation)
        return ResponseEntity.ok(evaluation)
    }
}