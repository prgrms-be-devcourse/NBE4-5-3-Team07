package com.java.NBE4_5_3_7.domain.news

import com.java.NBE4_5_3_7.domain.news.service.NewsService
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class NewsServiceTest {

    private lateinit var newsService: NewsService

    @Test
    @DisplayName("뉴스 검색 결과가 파싱되어 반환된다")
    fun `뉴스 검색 결과가 파싱되어 반환된다`() {
        val result = newsService.getNaverNews("인공지능", 1)

        assertThat(result).isNotNull
        assertThat(result?.items?.first()?.title)
    }
}