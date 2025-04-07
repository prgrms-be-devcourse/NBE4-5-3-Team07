package com.java.NBE4_5_3_7.domain.interview.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.KeywordContentRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.RandomRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewResponseDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.RandomResponseDto
import com.java.NBE4_5_3_7.domain.interview.service.InterviewService
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class InterviewControllerTest {

    private lateinit var mockMvc: MockMvc

    private val interviewService: InterviewService = mockk()
    private val memberService: MemberService = mockk()
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        val controller = InterviewController(interviewService, memberService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    @DisplayName("GET /interview/all → 모든 head ID 리스트 반환")
    fun getAllHeadQuestionIds() {
        every { interviewService.allHeadQuestion() } returns listOf(1L, 2L, 3L)

        // when
        val result = mockMvc.get("/interview/all") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, Array<Long>::class.java)

        assertThat(parsed).containsExactly(1L, 2L, 3L)
    }

    @Test
    @DisplayName("GET /interview/category/{category} → 카테고리 별 머리 질문 ID 리스트 반환")
    fun getCategoryHeadQuestionIds() {
        // given
        val category = InterviewCategory.DATABASE  // 예시로 TECH 카테고리 사용
        val mockResponse = listOf(4L, 5L, 6L)
        every { interviewService.categoryHeadQuestion(category) } returns mockResponse

        // when
        val result = mockMvc.get("/interview/category/$category") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, Array<Long>::class.java)

        assertThat(parsed).containsExactly(4L, 5L, 6L)
    }

    @Test
    @DisplayName("GET /interview/{id} → 특정 면접 콘텐츠 단건 조회")
    fun getInterviewContentById() {
        // given
        val interviewId = 1L
        val mockMemberId = 123L
        val mockResponse = InterviewResponseDto(
            id = interviewId,
            head_id = 100L,
            tail_id = 101L,
            question = "오라클 시퀀스(Oracle Sequence) 는 무엇인가요?",
            model_answer = "UNIQUE한 값을 생성해주는 오라클 객체. 시퀀스를 생성하면 PK와 같이 순차적으로 증가하는 컬럼을 자동 생성할 수 있다.",
            category = "DATABASE",
            keyword = "sequence",
            next_id = 2L,
            likeCount = 10L,
            likedByUser = false
        )

        // memberService.getIdFromRq()를 mock, 회원 ID를 123으로 설정
        every { memberService.getIdFromRq() } returns mockMemberId

        // interviewService.showOneInterviewContent(id, memberId) 호출 시 mockResponse 반환
        every { interviewService.showOneInterviewContent(interviewId, mockMemberId) } returns mockResponse

        // when
        val result = mockMvc.get("/interview/$interviewId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, InterviewResponseDto::class.java)

        assertThat(parsed.id).isEqualTo(interviewId)
        assertThat(parsed.head_id).isEqualTo(100L)
        assertThat(parsed.tail_id).isEqualTo(101L)
        assertThat(parsed.question).isEqualTo("오라클 시퀀스(Oracle Sequence) 는 무엇인가요?")
        assertThat(parsed.model_answer).isEqualTo("UNIQUE한 값을 생성해주는 오라클 객체. 시퀀스를 생성하면 PK와 같이 순차적으로 증가하는 컬럼을 자동 생성할 수 있다.")
        assertThat(parsed.category).isEqualTo("DATABASE")
        assertThat(parsed.keyword).isEqualTo("sequence")
        assertThat(parsed.next_id).isEqualTo(2L)
        assertThat(parsed.likeCount).isEqualTo(10L)
        assertThat(parsed.likedByUser).isFalse
    }

    @Test
    @DisplayName("POST /interview/random → 랜덤 면접 컨텐츠 반환")
    fun getRandomInterviewContent() {
        // given
        val randomRequestDto = RandomRequestDto(indexList = mutableListOf(1L, 2L, 3L))

        val interviewResponseDto = InterviewResponseDto(
            id = 1L,
            question = "Random Interview Question",
            model_answer = "This is the model answer.",
            category = "Tech",
            keyword = "Mock",
            next_id = 2L
        )

        val randomResponseDto = RandomResponseDto(
            indexList = listOf(1L, 2L, 3L),
            interviewResponseDto = interviewResponseDto
        )

        // interviewService가 showRandomInterviewContent를 호출할 때 위 응답을 반환하도록 mock 설정
        every { interviewService.showRandomInterviewContent(randomRequestDto, 1L) } returns randomResponseDto

        // memberService가 getIdFromRq() 호출 시 1L을 반환하도록 mock 설정
        every { memberService.getIdFromRq() } returns 1L

        // when
        val result = mockMvc.post("/interview/random") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(randomRequestDto)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then: 응답 본문을 파싱하여 검증
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, RandomResponseDto::class.java)

        assertThat(parsed.indexList).containsExactly(1L, 2L, 3L)
        assertThat(parsed.interviewResponseDto?.id).isEqualTo(1L)
        assertThat(parsed.interviewResponseDto?.question).isEqualTo("Random Interview Question")
        assertThat(parsed.interviewResponseDto?.next_id).isEqualTo(2L)
    }

    @Test
    @DisplayName("GET /interview/keyword → 키워드 리스트 반환")
    fun getKeywordList() {
        // given
        val mockResponse = listOf("DATABASE", "NETWORK", "OperatingSystem", "SPRING")

        // interviewService의 showKeywordList가 호출될 때 mockResponse를 반환하도록 설정
        every { interviewService.showKeywordList() } returns mockResponse

        // when
        val result = mockMvc.get("/interview/keyword") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, Array<String>::class.java)

        assertThat(parsed).containsExactly("DATABASE", "NETWORK", "OperatingSystem", "SPRING")
    }

    @Test
    @DisplayName("POST /interview/keyword/content → Keyword 포함된 머리 질문 ID 리스트 반환")
    fun getKeywordContentIds() {
        // given
        val requestDto = KeywordContentRequestDto(keywordList = listOf("NETWORK", "OperatingSystem"))
        val mockResponse = listOf(101L, 102L, 103L)

        every { interviewService.keywordHeadQuestion(requestDto) } returns mockResponse

        // when: 실제 API 호출
        val result = mockMvc.post("/interview/keyword/content") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestDto)
        }.andExpect {
            status { isOk() }
        }.andReturn()

        // then
        val body = result.response.contentAsString
        val parsed = objectMapper.readValue(body, Array<Long>::class.java)

        assertThat(parsed).containsExactly(101L, 102L, 103L)
    }
}