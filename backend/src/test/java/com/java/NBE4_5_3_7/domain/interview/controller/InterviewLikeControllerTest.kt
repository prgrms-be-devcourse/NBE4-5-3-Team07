package com.java.NBE4_5_3_7.domain.interview.controller

import com.java.NBE4_5_3_7.domain.interview.service.InterviewLikeService
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.nio.charset.StandardCharsets

class InterviewLikeControllerTest {

    private lateinit var mockMvc: MockMvc
    private val likeService: InterviewLikeService = mockk()
    private val memberService: MemberService = mockk()

    @BeforeEach
    fun setUp() {
        val controller = InterviewLikeController(likeService, memberService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    @DisplayName("GET /interview/like → 좋아요 추가 응답 반환")
    fun toggleLike() {
        // given
        val interviewId = 1L
        val memberId = 100L
        val mockResponse = "좋아요 추가"

        every { memberService.getIdFromRq() } returns memberId
        every { likeService.interviewLike(memberId, interviewId) } returns mockResponse

        // when
        val result = mockMvc.get("/interview/like?id=$interviewId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.getContentAsString(StandardCharsets.UTF_8)
        assertThat(body).isEqualTo(mockResponse)
    }

    @Test
    @DisplayName("GET /interview/like → 좋아요 취소 응답 반환")
    fun cancelLike() {
        // given
        val interviewId = 2L
        val memberId = 101L
        val mockResponse = "좋아요 취소"

        every { memberService.getIdFromRq() } returns memberId
        every { likeService.interviewLike(memberId, interviewId) } returns mockResponse

        // when
        val result = mockMvc.get("/interview/like?id=$interviewId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.getContentAsString(StandardCharsets.UTF_8)
        assertThat(body).isEqualTo(mockResponse)
    }
}
