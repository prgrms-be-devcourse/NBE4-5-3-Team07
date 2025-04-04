package com.java.NBE4_5_3_7.domain.news.controller

import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobResponseDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobsDetailDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.NewResponseDto
import com.java.NBE4_5_3_7.domain.news.service.NewsService
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
class NewsController(private val newsService: NewsService) {

    @GetMapping
    fun getNews(
        @RequestParam keyWord: String,
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<NewResponseDto> {
        return ResponseEntity.ok(newsService.getNaverNews(keyWord, page))
    }

    @GetMapping("/jobs")
    fun getJobs(
        @RequestParam ncsCdLst: String,
        @RequestParam(defaultValue = "1") page: Int
    ): ResponseEntity<JobResponseDto> {
        return ResponseEntity.ok(newsService.getJobList(ncsCdLst, page))
    }

    @GetMapping("/jobs/detail/{recrutPblntSn}")
    fun getJobsDetails(@PathVariable recrutPblntSn: String): ResponseEntity<JobsDetailDto> {
        return ResponseEntity.ok(newsService.getJobDetail(recrutPblntSn))
    }
}
