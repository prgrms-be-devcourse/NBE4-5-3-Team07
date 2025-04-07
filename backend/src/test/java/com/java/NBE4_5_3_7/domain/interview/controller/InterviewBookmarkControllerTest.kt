package com.java.NBE4_5_3_7.domain.interview.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto
import com.java.NBE4_5_3_7.domain.interview.service.InterviewService
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.nio.charset.StandardCharsets

class InterviewBookmarkControllerTest {

    private lateinit var mockMvc: MockMvc

    private val interviewService: InterviewService = mockk()
    private val memberService: MemberService = mockk()
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        val controller = InterviewBookmarkController(interviewService, memberService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    @DisplayName("GET /interview/bookmark → 사용자의 북마크 목록 반환")
    fun getBookmarks() {
        // given
        val memberId = 1L
        val mockBookmarks = listOf(
            BookmarkResponseDto(contentId = 101L, question = "DB란?", answer = "DB에 대한 설명"),
            BookmarkResponseDto(contentId = 102L, question = "OS란?", answer = "OS에 대한 설명")
        )

        every { memberService.getIdFromRq() } returns memberId
        every { interviewService.showMyBookmark(memberId) } returns mockBookmarks

        // when
        val result = mockMvc.get("/interview/bookmark") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, Array<BookmarkResponseDto>::class.java)

        assertThat(parsed).hasSize(2)
        assertThat(parsed[0].contentId).isEqualTo(101L)
        assertThat(parsed[1].question).isEqualTo("OS란?")
    }

    @Test
    @DisplayName("POST /interview/bookmark → 북마크 추가")
    fun addBookmark() {
        // given
        val contentId = 1L
        val mockMemberId = 123L
        val mockResponse = "북마크가 추가되었습니다."

        every { memberService.getIdFromRq() } returns mockMemberId
        every { interviewService.bookmark(mockMemberId, contentId) } returns mockResponse

        // when
        val result = mockMvc.post("/interview/bookmark?id=$contentId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.getContentAsString(StandardCharsets.UTF_8)
        assertThat(body).isEqualTo(mockResponse)
    }

    @Test
    @DisplayName("DELETE /interview/bookmark/{noteId} → 북마크 삭제")
    fun deleteBookmark() {
        // given
        val noteId = 1L
        val mockMember = mockk<Member>()
        val mockResponse = "북마크가 삭제되었습니다."

        every { memberService.getMemberFromRq() } returns mockMember
        every { interviewService.deleteNote(noteId, mockMember) } returns Unit

        // when
        val result = mockMvc.delete("/interview/bookmark/$noteId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.getContentAsString(StandardCharsets.UTF_8)
        assertThat(body).isEqualTo(mockResponse)
    }
}