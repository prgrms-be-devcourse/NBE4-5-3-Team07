package com.java.NBE4_5_3_7.domain.study.controller

import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentUpdateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.response.StudyContentDetailDto
import com.java.NBE4_5_3_7.domain.study.service.StudyContentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/study")
class StudyContentController(private val studyContentService: StudyContentService) {
    @get:GetMapping
    val firstCategory: ResponseEntity<List<String>>
        get() = ResponseEntity.ok(studyContentService.firstCategory)

    @get:GetMapping("/all")
    val allCategory: ResponseEntity<Map<String, List<String?>?>>
        get() = ResponseEntity.ok(
            studyContentService.allCategory
        )

    @GetMapping("/{firstCategory}")
    fun getSecondCategory(@PathVariable firstCategory: String): ResponseEntity<List<String?>?> {
        return ResponseEntity.ok(studyContentService.getSecondCategoryByFirstCategory(firstCategory))
    }

    // 다건 조회
    @GetMapping("/{firstCategory}/{secondCategory}")
    fun getStudyContentByCategory(
        @PathVariable firstCategory: String,
        @PathVariable secondCategory: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "1") size: Int
    ): ResponseEntity<Page<StudyContentDetailDto>> {
        val studyContents = studyContentService
            .getStudyContentsByCategory(firstCategory, secondCategory, PageRequest.of(page, size))

        return ResponseEntity.ok(studyContents)
    }

    @PutMapping("/update/{studyContentId}")
    fun updateStudyContent(
        @PathVariable("studyContentId") studyContentId: Long,
        @RequestBody requestDto: StudyContentUpdateRequestDto
    ): ResponseEntity<String> {
        studyContentService.updateStudyContent(studyContentId, requestDto.updateContent)
        return ResponseEntity.ok("update success")
    }

    @DeleteMapping("/delete/{studyContentId}")
    fun deleteStudyContent(@PathVariable("studyContentId") studyContentId: Long): ResponseEntity<String> {
        studyContentService.deleteStudyContent(studyContentId)
        return ResponseEntity.ok("delete studyContent")
    }
}
