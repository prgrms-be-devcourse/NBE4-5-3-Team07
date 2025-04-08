package com.java.NBE4_5_3_7.domain.news

import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobResponseDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobsDetailDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.NewResponseDto
import com.java.NBE4_5_3_7.domain.news.service.NewsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class NewsServiceTest {

    @Autowired
    private lateinit var newsService: NewsService

    @Test
    @DisplayName("네이버 뉴스 API 호출 테스트")
    fun testNaverNewsApiCall() {
        val keyword = "인공지능"
        val page = 1

        val response: NewResponseDto? = newsService.getNaverNews(keyword, page)

        val allContainKeyword = response?.items?.all { it.description?.contains(keyword) == true } ?: false
        val firstItem = response?.items?.firstOrNull()
        println(
            """
            📌 News Item:
            ▪ Title: ${firstItem?.title}
            ▪ Link: ${firstItem?.link}
            ▪ Description: ${firstItem?.description}
            ▪ PubDate: ${firstItem?.pubDate}
            """.trimIndent()
        )
        assertThat(response).isNotNull
        assertThat(response?.items).isNotEmpty
        assertThat(allContainKeyword).isTrue()
    }

    @Test
    @DisplayName("공공 데이터 구인공고 리스트 API 호출 테스트")
    fun testPublicJobListApiCall() {
        val ncsCdLst = "R600006"
        val page = 1

        val response: JobResponseDto? = newsService.getJobList(ncsCdLst, page)

        val firstJob = response?.result?.firstOrNull()

        println(
            """
            📌 Job Posting:
            ▪ Title: ${firstJob?.recrutPbancTtl}
            ▪ No.${firstJob?.recrutPblntSn}
            ▪ Institute: ${firstJob?.instNm}
            ▪ Application Period: ${firstJob?.pbancBgngYmd} ~ ${firstJob?.pbancEndYmd}
            ▪ Link: ${firstJob?.srcUrl}
            """.trimIndent()
        )

        assertThat(response).isNotNull
        assertThat(response?.result).isNotEmpty
    }

    @Test
    @DisplayName("공공 데이터 구인공고 상세 API 호출 테스트")
    fun testPublicJobDetailApiCall() {
        val recrutPblntSn = "284270"

        val response: JobsDetailDto? = newsService.getJobDetail(recrutPblntSn)

        val allStepsHaveTitles = response?.steps?.all { it.recrutPbancTtl != null } ?: true

        println(
            """
            📌 Job Detail:
            ▪ Title: ${response?.recrutPbancTtl}
            ▪ Institute: ${response?.instNm}
            ▪ Region: ${response?.workRgnNmLst}
            ▪ Application Period: ${response?.pbancBgngYmd} ~ ${response?.pbancEndYmd}
            ▪ File Count: ${response?.files?.size ?: 0}
            ▪ Step Count: ${response?.steps?.size ?: 0}
            """.trimIndent()
        )

        assertThat(response).isNotNull
        assertThat(response?.instNm).isNotNull
        assertThat(allStepsHaveTitles).isTrue()
    }
}
