package com.java.NBE4_5_3_7.domain.study.controller

import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentCreateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentUpdateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.response.StudyContentDetailDto
import com.java.NBE4_5_3_7.domain.study.service.StudyContentAdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/study")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "학습 콘텐츠 관리", description = "관리자용 API")
class StudyContentAdminController(private val studyContentAdminService: StudyContentAdminService) {
    @get:GetMapping("/all")
    @get:Operation(summary = "모든 카테고리 조회", description = "첫 번째 및 두 번째 카테고리 목록을 조회합니다.")
    val allCategory: ResponseEntity<Map<String, List<String?>?>>
        get() = ResponseEntity.ok(
            studyContentAdminService.allCategory
        )

    @Operation(summary = "첫 번째 카테고리별 학습 콘텐츠 조회", description = "지정된 첫 번째 카테고리에 속하는 학습 콘텐츠를 페이징하여 조회합니다.")
    @GetMapping("/category/{firstCategory}")
    fun getPagedStudyContentsByFirstCategory(
        @PathVariable firstCategory: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<StudyContentDetailDto>> {
        val studyContents = studyContentAdminService
            .getPagedStudyContentsByCategory(firstCategory, PageRequest.of(page, size))

        return ResponseEntity.ok(studyContents)
    }

    @Operation(summary = "첫 번째 + 두 번째 카테고리별 학습 콘텐츠 조회", description = "지정된 첫 번째 및 두 번째 카테고리에 속하는 학습 콘텐츠를 페이징하여 조회합니다.")
    @GetMapping("/category/{firstCategory}/{secondCategory}")
    fun getPagedStudyContentsByCategories(
        @PathVariable firstCategory: String?,
        @PathVariable secondCategory: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<StudyContentDetailDto>> {
        val studyContents = studyContentAdminService
            .getPagedStudyContentsByCategories(firstCategory, secondCategory, PageRequest.of(page, size))

        return ResponseEntity.ok(studyContents)
    }

    @Operation(summary = "학습 콘텐츠 상세 조회", description = "지정된 ID의 학습 콘텐츠 정보를 조회합니다.")
    @GetMapping("/{studyContentId}")
    fun getStudyContentById(@PathVariable studyContentId: Long): ResponseEntity<StudyContentDetailDto> {
        return ResponseEntity.ok(studyContentAdminService.getStudyContentById(studyContentId))
    }

    @Operation(summary = "학습 콘텐츠 수정", description = "지정된 ID의 학습 콘텐츠를 수정합니다.")
    @PutMapping("/{studyContentId}")
    fun updateStudyContent(
        @PathVariable studyContentId: Long,
        @RequestBody requestDto: StudyContentUpdateRequestDto
    ): ResponseEntity<String> {
        studyContentAdminService.updateStudyContent(studyContentId, requestDto)
        return ResponseEntity.ok("update success")
    }

    @Operation(summary = "학습 콘텐츠 삭제", description = "지정된 ID의 학습 콘텐츠를 삭제합니다.")
    @DeleteMapping("/{studyContentId}")
    fun deleteStudyContent(@PathVariable studyContentId: Long): ResponseEntity<String> {
        studyContentAdminService.deleteStudyContent(studyContentId)
        return ResponseEntity.ok("delete success")
    }

    @Operation(
        summary = "학습 콘텐츠 등록",
        description = """
        새로운 학습 콘텐츠를 등록합니다.
        
        - `firstCategory`는 기존에 존재하는 카테고리 중에서만 선택 가능합니다.
        - `secondCategory`, `title`, `body`는 빈 문자열 또는 `null`일 수 없습니다.
    """
    )
    @PostMapping
    fun createStudyContent(
        @RequestBody requestDto: StudyContentCreateRequestDto
    ): ResponseEntity<StudyContentDetailDto> {
        return ResponseEntity.ok(studyContentAdminService.createStudyContent(requestDto))
    }
}