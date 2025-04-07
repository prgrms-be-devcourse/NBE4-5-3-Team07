package com.java.NBE4_5_3_7.domain.study.controller

import com.java.NBE4_5_3_7.domain.study.service.StudyMemoLikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/studyMemo/like")
class StudyMemoLikeController(private val studyMemoLikeService: StudyMemoLikeService) {
    @PostMapping("/{studyMemoId}")
    fun saveStudyMemoLike(@PathVariable studyMemoId: Long): ResponseEntity<String> {
        return ResponseEntity.ok(studyMemoLikeService.memoLike(studyMemoId))
    }
}
