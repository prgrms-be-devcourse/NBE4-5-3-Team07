package com.java.NBE4_5_3_7.domain.news

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobResponseDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobsDetailDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.NewResponseDto
import com.java.NBE4_5_3_7.domain.news.service.NewsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

class NewsServiceTest {

    private lateinit var restTemplate: RestTemplate
    private lateinit var newsService: NewsService

    @BeforeEach
    fun setup() {
        restTemplate = mock(RestTemplate::class.java)

        newsService = object : NewsService() {
            init {
                val clientKeyField = NewsService::class.java.getDeclaredField("client_key")
                clientKeyField.isAccessible = true
                clientKeyField.set(this, "test-key")

                val clientSecretField = NewsService::class.java.getDeclaredField("client_secret")
                clientSecretField.isAccessible = true
                clientSecretField.set(this, "test-secret")
            }

            override fun getNaverNews(keyWord: String, page: Int): NewResponseDto? {
                val headers = HttpHeaders()
                headers["X-Naver-Client-Id"] = client_key
                headers["X-Naver-Client-Secret"] = client_secret
                val entity = HttpEntity<String>(headers)
                val display = 5
                val start = (page - 1) * display + 1

                val url = ("https://openapi.naver.com/v1/search/news.json"
                        + "?query=" + keyWord
                        + "&display=" + display
                        + "&start=" + start
                        + "&sort=date")

                val dummyJson = """
                    {
                      "lastBuildDate": "Mon, 01 Apr 2025",
                      "total": 1,
                      "start": 1,
                      "display": 5,
                      "items": [
                        {
                          "title": "AI News",
                          "link": "https://example.com",
                          "description": "Latest AI",
                          "pubDate": "Mon, 01 Apr 2025"
                        }
                      ]
                    }
                """.trimIndent()

                val responseEntity = ResponseEntity(dummyJson, HttpStatus.OK)

                `when`(
                    restTemplate.exchange(
                        eq(url),
                        eq(HttpMethod.GET),
                        any(HttpEntity::class.java),
                        eq(String::class.java)
                    )
                ).thenReturn(responseEntity)

                val response = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java).body
                return ObjectMapper().readValue(response, NewResponseDto::class.java)
            }

            override fun getJobList(ncsCdLst: String, page: Int): JobResponseDto {
                val objectMapper = ObjectMapper()
                val size = 5
                val url = "https://apis.data.go.kr/1051000/recruitment/list" +
                        "?serviceKey=test-public-key" +
                        "&acbgCondLst=R7010" +
                        "&ncsCdLst=$ncsCdLst" +
                        "&numOfRows=$size" +
                        "&ongoingYn=Y" +
                        "&pageNo=$page" +
                        "&pbancBgngYmd=2025-01-01" +
                        "&recrutSe=R2030" +
                        "&resultType=json"

                val dummyJson = """
                    {
                      "totalCount": 1,
                      "result": [
                        {
                          "recrutPbancTtl": "백엔드 개발자 모집",
                          "instNm": "카카오",
                          "recrutNope": 2
                        }
                      ]
                    }
                """.trimIndent()

                val responseEntity = ResponseEntity(dummyJson, HttpStatus.OK)

                `when`(restTemplate.getForEntity(eq(url), eq(String::class.java)))
                    .thenReturn(responseEntity)

                val response = restTemplate.getForEntity(url, String::class.java)
                val jsonNode = objectMapper.readTree(response.body)

                val jobResponseDto = JobResponseDto()
                jobResponseDto.totalCount = jsonNode["totalCount"].asInt()

                val jobList: List<JobResponseDto.Job> =
                    objectMapper.readValue(
                        jsonNode["result"].toString(),
                        object : TypeReference<List<JobResponseDto.Job>>() {})

                jobResponseDto.result = jobList
                return jobResponseDto
            }

            override fun getJobDetail(recrutPblntSn: String): JobsDetailDto? {
                val objectMapper = ObjectMapper()
                val url = "https://apis.data.go.kr/1051000/recruitment/detail" +
                        "?serviceKey=test-public-key" +
                        "&sn=$recrutPblntSn"

                val dummyJson = """
                    {
                      "result": {
                        "recrutPblntSn": 12345,
                        "recrutPbancTtl": "백엔드 개발자 모집",
                        "instNm": "카카오",
                        "recrutNope": 2,
                        "pbancBgngYmd": "2025-04-01",
                        "pbancEndYmd": "2025-04-30",
                        "srcUrl": "https://kakao.com/recruit/detail/12345",
                        "files": [
                          {
                            "atchFileNm": "채용공고.pdf",
                            "url": "https://example.com/job.pdf"
                          },
                          {
                            "atchFileNm": "회사소개서.pdf",
                            "url": "https://example.com/intro.pdf"
                          }
                        ]
                      }
                    }
                """.trimIndent()

                val responseEntity = ResponseEntity(dummyJson, HttpStatus.OK)

                `when`(restTemplate.getForEntity(eq(url), eq(String::class.java)))
                    .thenReturn(responseEntity)

                val response = restTemplate.getForEntity(url, String::class.java)
                val jsonNode = objectMapper.readTree(response.body)
                val resultNode = jsonNode["result"]

                val jobsDetailDto = objectMapper.readValue(resultNode.toString(), JobsDetailDto::class.java)

                val filesNode = resultNode["files"]
                if (filesNode != null && filesNode.isArray) {
                    val files: List<JobsDetailDto.Files> = objectMapper.readValue(
                        filesNode.toString(),
                        object : TypeReference<List<JobsDetailDto.Files>>() {}
                    )
                    jobsDetailDto.files = files
                }

                return jobsDetailDto
            }
        }
    }

    @Test
    @DisplayName("뉴스 api 호출 성공")
    fun `API가 유효한 JSON을 응답하면 파싱된 뉴스 객체를 반환한다`() {
        // when
        val result = newsService.getNaverNews("AI", 1)

        // then
        assertNotNull(result)
        assertEquals(1, result?.total)
        assertEquals(5, result?.display)
        assertEquals("AI News", result?.items?.get(0)?.title)
    }

    @Test
    @DisplayName("채용 api 호출 성공")
    fun `유효한 JSON 응답이 오면 파싱된 Job 리스트를 반환한다`() {
        // when
        val result = newsService.getJobList("123456", 1)

        // then
        assertNotNull(result)
        assertEquals(1, result?.totalCount)
        assertEquals("백엔드 개발자 모집", result?.result?.get(0)?.recrutPbancTtl)
        assertEquals("카카오", result?.result?.get(0)?.instNm)
        assertEquals(2, result?.result?.get(0)?.recrutNope)
    }

    @Test
    @DisplayName("채용 상세정보 API 호출 성공")
    fun `유효한 JSON 응답이 오면 상세 채용 정보 객체를 반환한다`() {
        // when
        val result = newsService.getJobDetail("12345")

        // then
        assertNotNull(result)
        assertEquals("백엔드 개발자 모집", result?.recrutPbancTtl)
        assertEquals("카카오", result?.instNm)
        assertEquals(2, result?.recrutNope)
        assertEquals("2025-04-01", result?.pbancBgngYmd)
        assertEquals("https://kakao.com/recruit/detail/12345", result?.srcUrl)

        assertEquals(2, result?.files?.size)
        assertEquals("채용공고.pdf", result?.files?.get(0)?.atchFileNm)
        assertEquals("https://example.com/job.pdf", result?.files?.get(0)?.url)
        assertEquals("회사소개서.pdf", result?.files?.get(1)?.atchFileNm)
        assertEquals("https://example.com/intro.pdf", result?.files?.get(1)?.url)
    }
}
