package com.java.NBE4_5_3_7.domain.interview.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.java.NBE4_5_3_7.domain.interview.service.InterviewAdminService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class InterviewAdminControllerTest {

    private lateinit var mockMvc: MockMvc
    private val interviewAdminService: InterviewAdminService = mockk()
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        val controller = InterviewAdminController(interviewAdminService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("GET /api/v1/admin/interview/all → 카테고리별 키워드 목록 조회")
    fun getCategoryKeywords() {
        // given
        val mockResponse = mapOf(
            "DATABASE" to listOf("SQL", "Index"),
            "NETWORK" to listOf("TCP/IP", "DNS")
        )
        every { interviewAdminService.categoryKeywords } returns mockResponse

        // when
        val result = mockMvc.get("/api/v1/admin/interview/all") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, Map::class.java)

        assertThat(parsed).isNotEmpty
        assertThat((parsed["DATABASE"] as List<*>)).contains("SQL", "Index")
        assertThat((parsed["NETWORK"] as List<*>)).contains("TCP/IP", "DNS")
    }
}