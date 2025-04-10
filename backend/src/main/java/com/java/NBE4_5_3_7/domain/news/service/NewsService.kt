package com.java.NBE4_5_3_7.domain.news.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobResponseDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.JobsDetailDto
import com.java.NBE4_5_3_7.domain.news.dto.responseDto.NewResponseDto
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class NewsService {
    @Value("\${naver.key}")
    protected val client_key: String? = null

    @Value("\${naver.secret}")
    protected val client_secret: String? = null

    @Value("\${publicData.key}")
    protected val public_data_key: String? = null

    @CircuitBreaker(name = "naverNews", fallbackMethod = "naverNewsFallback")
    fun getNaverNews(keyWord: String, page: Int): NewResponseDto? {
        val restTemplate = RestTemplate()
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

        val responseEntity = restTemplate.exchange(
            url, HttpMethod.GET, entity,
            String::class.java
        )
        val response = responseEntity.body

        val objectMapper = ObjectMapper()
        var newsResponse: NewResponseDto? = null

        try {
            newsResponse = objectMapper.readValue(response, NewResponseDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return newsResponse
    }

    fun naverNewsFallback(keyWord: String, page: Int, t: Throwable): ResponseEntity<NewResponseDto> {
        val fallbackDto = NewResponseDto().apply {
            errorMessage = "현재 채용공고 호출이 불가능합니다. 잠시 후 다시 시도해주세요."
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackDto)
    }

    @CircuitBreaker(name = "jobList", fallbackMethod = "jobListFallback")
    fun getJobList(ncsCdLst: String, page: Int): JobResponseDto? {
        val restTemplate = RestTemplate()
        val objectMapper = ObjectMapper()
        val jobResponseDto = JobResponseDto()
        val size = 5

        val url = "https://apis.data.go.kr/1051000/recruitment/list" +
                "?serviceKey=" + public_data_key +
                "&acbgCondLst=R7010" +
                "&ncsCdLst=" + ncsCdLst +
                "&numOfRows=" + size +
                "&ongoingYn=Y" +
                "&pageNo=" + page +
                "&pbancBgngYmd=2025-01-01" +
                "&recrutSe=R2030" +
                "&resultType=json"

        try {
            val response = restTemplate.getForEntity(url, String::class.java)
            val jsonNode = objectMapper.readTree(response.body)

            jobResponseDto.totalCount = jsonNode["totalCount"].asInt()

            val jobList: List<JobResponseDto.Job> = if (jsonNode["result"] != null) {
                objectMapper.readValue(jsonNode["result"].toString(), object : TypeReference<List<JobResponseDto.Job>>() {})
            } else {
                emptyList()
            }

            jobResponseDto.result = jobList

            return jobResponseDto
        } catch (e: Exception) {
            throw RuntimeException("채용 공고 데이터 호출 실패", e)
        }
    }

    fun jobListFallback(ncsCdLst: String, page: Int, t: Throwable): ResponseEntity<JobResponseDto> {
        val fallbackDto = JobResponseDto().apply {
            totalCount = 0
            result = emptyList()
            errorMessage = "현재 채용공고 호출이 불가능합니다. 잠시 후 다시 시도해주세요."
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackDto)
    }


    @CircuitBreaker(name = "jobDetail", fallbackMethod = "jobDetailFallback")
    fun getJobDetail(recrutPblntSn: String): JobsDetailDto? {
        val restTemplate = RestTemplate()
        val objectMapper = ObjectMapper()
        val jobsDetailDto: JobsDetailDto
        val url = "https://apis.data.go.kr/1051000/recruitment/detail" +
                "?serviceKey=" + public_data_key +
                "&sn=" + recrutPblntSn

        try {
            val response = restTemplate.getForEntity(url, String::class.java)

            val jsonNode = objectMapper.readTree(response.body)
            val resultNode = jsonNode["result"]
            jobsDetailDto = objectMapper.readValue(resultNode.toString(), JobsDetailDto::class.java)

            val filesNode = resultNode["files"]
            if (filesNode != null && filesNode.isArray) {
                val files: List<JobsDetailDto.Files> = objectMapper.readValue(
                    filesNode.toString(), object : TypeReference<List<JobsDetailDto.Files>>() {}
                )
                jobsDetailDto.files = files
            }

            return jobsDetailDto
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun jobDetailFallback(recrutPblntSn: String, t: Throwable): ResponseEntity<JobsDetailDto> {
        val fallbackDto = JobsDetailDto().apply {
            errorMessage = "현재 채용공고 호출이 불가능합니다. 잠시 후 다시 시도해주세요."
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackDto)
    }
}