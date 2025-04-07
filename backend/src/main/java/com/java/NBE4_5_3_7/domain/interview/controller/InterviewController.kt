package com.java.NBE4_5_3_7.domain.interview.controller

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.KeywordContentRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.RandomRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewResponseDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.RandomResponseDto
import com.java.NBE4_5_3_7.domain.interview.service.InterviewService
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/interview")
class InterviewController(
    private val service: InterviewService,
    private val memberService: MemberService
) {

    // 전체 머리 질문 ID (로그인 검증 적용)
    @GetMapping("/all")
    fun allHeadContent(): ResponseEntity<List<Long>> {
        return ResponseEntity.ok(service.allHeadQuestion())
    }

    // 카테고리 별 머리 질문 ID
    @GetMapping("/category/{category}")
    fun categoryContentId(@PathVariable category: InterviewCategory): ResponseEntity<List<Long>> {
        return ResponseEntity.ok(service.categoryHeadQuestion(category))
    }

    // 특정 ID 면접 컨텐츠 단건 조회 -> 다음 면접 컨텐츠 ID 값은 ID 순서대로 제공
    @GetMapping("/{id}")
    fun oneContent(@PathVariable id: Long): ResponseEntity<InterviewResponseDto> {
        val memberId = memberService.getIdFromRq()
        return ResponseEntity.ok(service.showOneInterviewContent(id, memberId))
    }

    // 특정 ID 면접 컨텐츠 단건 조회 -> 다음 면접 컨텐츠 ID 값은 랜덤하게 제공
    @PostMapping("/random")
    fun randomContent(@RequestBody dto: RandomRequestDto): ResponseEntity<RandomResponseDto> {
        val memberId = memberService.getIdFromRq()
        return ResponseEntity.ok(service.showRandomInterviewContent(dto, memberId))
    }

    // Keyword 리스트 반환
    @GetMapping("/keyword")
    fun showKeywordList(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(service.showKeywordList())
    }

    // Keyword 포함된 머리 질문들의 ID 값 리스트 반환
    @PostMapping("/keyword/content")
    fun keywordContentId(@RequestBody dto: KeywordContentRequestDto): ResponseEntity<List<Long>> {
        return ResponseEntity.ok(service.keywordHeadQuestion(dto))
    }
}