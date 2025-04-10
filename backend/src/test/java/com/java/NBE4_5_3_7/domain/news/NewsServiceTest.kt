package com.java.NBE4_5_3_7.domain.news

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NewsServiceTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var basePath: String

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        basePath = "/api/v1/news"
    }

    @Test
    @DisplayName("네이버 뉴스 API 호출 E2E 테스트")
    fun testGetNaverNews() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .queryParam("keyWord", "인공지능")
            .queryParam("page", 1)
            .`when`()
            .get("$basePath")
            .then()
            .statusCode(200)
            .body("items", notNullValue())
            .body("items[0].description", containsString("인공지능"))
    }

    @Test
    @DisplayName("공공 데이터 구인공고 리스트 API 호출 E2E 테스트")
    fun testGetJobList() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .queryParam("ncsCdLst", "R600006")
            .queryParam("page", 1)
            .`when`()
            .get("$basePath/jobs")
            .then()
            .statusCode(200)
            .body("result", notNullValue())
    }

    @Test
    @DisplayName("공공 데이터 구인공고 상세 API 호출 E2E 테스트")
    fun testGetJobDetail() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("$basePath/jobs/detail/284270")
            .then()
            .statusCode(200)
            .body("instNm", notNullValue())
            .body("steps.size()", greaterThanOrEqualTo(0))
    }
}
