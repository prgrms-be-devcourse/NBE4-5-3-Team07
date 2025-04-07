package com.java.NBE4_5_3_7.domain.interview.controller

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewContentAdminResponseDto
import com.java.NBE4_5_3_7.domain.interview.service.InterviewAdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "면접 질문 관리", description = "관리자용 API")
@RestController
@RequestMapping("/api/v1/admin/interview")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
class InterviewAdminController(
    private val interviewAdminService: InterviewAdminService
) {

    @GetMapping("/all")
    @Operation(summary = "카테고리별 키워드 조회", description = "각 카테고리 내 키워드 목록을 조회합니다.")
    fun getCategoryKeywords(): ResponseEntity<Map<String, List<String>>> =
        ResponseEntity.ok(interviewAdminService.categoryKeywords)

    @GetMapping("/category/{category}")
    @Operation(summary = "특정 카테고리의 모든 질문 조회")
    fun getInterviewsByCategory(
        @Parameter(description = "조회할 카테고리", example = "DATABASE")
        @PathVariable category: InterviewCategory?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<InterviewContentAdminResponseDto>> =
        ResponseEntity.ok(interviewAdminService.getInterviewsByCategory(category, page, size))

    @GetMapping("/category/{category}/{keyword}")
    @Operation(summary = "특정 카테고리의 키워드에 해당하는 모든 질문 조회")
    fun getInterviewsByCategoryAndKeyword(
        @Parameter(description = "조회할 카테고리", example = "DATABASE")
        @PathVariable category: InterviewCategory?,
        @Parameter(description = "조회할 키워드", example = "sequence")
        @PathVariable keyword: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<InterviewContentAdminResponseDto>> =
        ResponseEntity.ok(interviewAdminService.getInterviewsByCategoryAndKeyword(category, keyword, page, size))

    @GetMapping("/{interviewContentId}")
    @Operation(summary = "특정 면접 질문 ID 조회")
    fun getInterviewContentById(
        @Parameter(description = "조회할 면접 질문 ID", example = "1")
        @PathVariable interviewContentId: Long
    ): ResponseEntity<InterviewContentAdminResponseDto> =
        ResponseEntity.ok(interviewAdminService.getInterviewContentById(interviewContentId))

    @GetMapping("/{interviewContentId}/related")
    @Operation(summary = "연관된 면접 질문 조회")
    fun getRelatedInterviewContents(
        @Parameter(description = "조회할 면접 질문 ID", example = "1")
        @PathVariable interviewContentId: Long
    ): ResponseEntity<List<InterviewContentAdminResponseDto>> =
        ResponseEntity.ok(interviewAdminService.getInterviewContentWithAllTails(interviewContentId))

    @PutMapping("/{interviewContentId}")
    @Operation(summary = "특정 면접 질문 수정")
    fun updateInterviewContent(
        @PathVariable interviewContentId: Long,
        @RequestBody requestDto: InterviewContentAdminRequestDto
    ): ResponseEntity<InterviewContentAdminResponseDto> =
        ResponseEntity.ok(interviewAdminService.updateInterviewContent(interviewContentId, requestDto))

    @DeleteMapping("/{interviewContentId}")
    @Operation(summary = "특정 면접 질문 삭제")
    fun deleteInterviewContent(@PathVariable interviewContentId: Long): ResponseEntity<Void> {
        interviewAdminService.deleteInterviewContentWithAllTails(interviewContentId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    @Operation(
        summary = "면접 질문 등록",
        description = """
            새로운 면접 질문을 등록합니다. 기존 질문의 꼬리 질문으로 추가할 수도 있습니다.
            - `headId`가 `null`이면 새로운 머리 질문이 됩니다.
            - `headId`가 존재하면, 해당 질문의 마지막 질문인지(`has_tail = 0`) 확인 후 꼬리 질문으로 추가합니다.
        """
    )
    fun createInterviewContent(
        @RequestBody requestDto: InterviewContentAdminRequestDto
    ): ResponseEntity<InterviewContentAdminResponseDto> =
        ResponseEntity.ok(interviewAdminService.createInterviewContent(requestDto))
}
