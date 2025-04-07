package com.java.NBE4_5_3_7.domain.interview.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentAdminRepository
import com.java.NBE4_5_3_7.global.exception.ServiceException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

class InterviewAdminServiceTest {

    private val repository: InterviewContentAdminRepository = mockk(relaxed = true)
    private lateinit var service: InterviewAdminService

    @BeforeEach
    fun setUp() {
        service = InterviewAdminService(repository)
    }

    @Test
    @DisplayName("카테고리 키워드 목록 조회 성공")
    fun getCategoryKeywords_success() {
        // given
        val category = InterviewCategory.DATABASE
        val keywords = listOf(
            "sequence", "DBMS", "view", "정규화", "이상현상", "DB설계",
            "무결성", "Oracle", "Transaction", "JDBC", "Index", "MySQL"
        )
        every { repository.findUniqueCategories() } returns listOf(category)
        every { repository.findUniqueKeywordsByCategory(category) } returns keywords

        // when
        val result = service.categoryKeywords

        // then
        assertThat(result).containsKey("DATABASE")
        val dbKeywords = requireNotNull(result["DATABASE"]) { "DATABASE 키워드가 존재하지 않습니다." }
        assertThat(dbKeywords).containsExactlyElementsOf(keywords)
    }

    @Test
    @DisplayName("카테고리 목록이 없는 경우 - 예외 발생")
    fun getCategoryKeywords_noCategories() {
        every { repository.findUniqueCategories() } returns emptyList()

        val exception = assertThrows<ServiceException> {
            service.categoryKeywords
        }

        assertThat(exception.message).contains("등록된 면접 질문 카테고리")
    }

    @Test
    @DisplayName("특정 카테고리에 키워드가 없는 경우 - 빈 리스트 반환")
    fun getCategoryKeywords_noContent() {
        // given
        val category = InterviewCategory.DATABASE
        every { repository.findUniqueCategories() } returns listOf(category)
        every { repository.findUniqueKeywordsByCategory(category) } returns emptyList()

        // when
        val result = service.categoryKeywords

        // then
        assertThat(result).containsKey("DATABASE")
        val dbKeywords = requireNotNull(result["DATABASE"]) { "DATABASE 키워드가 존재하지 않습니다." }
        assertThat(dbKeywords).isEmpty()
    }

    @Test
    @DisplayName("카테고리별 면접 질문 조회 성공")
    fun getInterviewsByCategory_success() {
        // given
        val category = InterviewCategory.SPRING
        val page = 0
        val size = 10

        val content = InterviewContent().apply {
            interviewContentId = 1L
            keyword = "DI"
            this.category = category
            question = "DI란 무엇인가?"
            modelAnswer = "DI는 의존성을 외부에서 주입하는 방식입니다."
            isHead = true
            hasTail = false
        }

        val contentPage = PageImpl(listOf(content))

        every { repository.findByCategory(eq(category), any<Pageable>()) } returns contentPage
        every { repository.countLikesByInterviewContentId(1L) } returns 5L

        // when
        val result = service.getInterviewsByCategory(category, page, size)

        // then
        assertThat(result.content).hasSize(1)
        val dto = result.content.first()
        assertThat(dto.id).isEqualTo(content.interviewContentId)
        assertThat(dto.likeCount).isEqualTo(5L)
    }

    @Test
    @DisplayName("카테고리별 면접 질문 조회 실패")
    fun getInterviewsByCategory_noContent() {
        // given
        val category = InterviewCategory.SPRING
        val emptyPage = PageImpl<InterviewContent>(emptyList())

        every { repository.findByCategory(eq(category), any<Pageable>()) } returns emptyPage

        // expect
        val exception = assertThrows<ServiceException> {
            service.getInterviewsByCategory(category, 0, 10)
        }

        assertThat(exception.message).contains("해당 카테고리에 속하는 면접 질문이 없습니다.")
    }

    @Test
    @DisplayName("카테고리와 키워드로 면접 질문 조회 성공")
    fun getInterviewsByCategoryAndKeyword_success() {
        // given
        val category = InterviewCategory.SPRING
        val keyword = "DI"
        val page = 0
        val size = 10

        val content = InterviewContent().apply {
            interviewContentId = 1L
            this.keyword = keyword
            this.category = category
            question = "DI란 무엇인가?"
            modelAnswer = "DI는 의존성을 외부에서 주입하는 방식입니다."
            isHead = true
            hasTail = false
        }

        val contentPage = PageImpl(listOf(content))

        every {
            repository.findByCategoryAndKeyword(eq(category), eq(keyword), any<Pageable>())
        } returns contentPage
        every { repository.countLikesByInterviewContentId(1L) } returns 7L

        // when
        val result = service.getInterviewsByCategoryAndKeyword(category, keyword, page, size)

        // then
        assertThat(result.content).hasSize(1)
        val dto = result.content.first()
        assertThat(dto.id).isEqualTo(1L)
        assertThat(dto.keyword).isEqualTo("DI")
        assertThat(dto.category).isEqualTo(InterviewCategory.SPRING)
        assertThat(dto.likeCount).isEqualTo(7L)
    }

    @Test
    @DisplayName("카테고리와 키워드로 면접 질문 조회 실패")
    fun getInterviewsByCategoryAndKeyword_noContent() {
        // given
        val category = InterviewCategory.SPRING
        val keyword = "DI"

        every {
            repository.findByCategoryAndKeyword(eq(category), eq(keyword), any<Pageable>())
        } returns Page.empty()

        // expect
        val exception = assertThrows<ServiceException> {
            service.getInterviewsByCategoryAndKeyword(category, keyword, 0, 10)
        }

        assertThat(exception.message).contains("해당 카테고리와 키워드를 포함하는 면접 질문이 없습니다.")
    }

    @Test
    @DisplayName("면접 질문 ID로 조회 성공")
    fun getInterviewContentById_success() {
        // given
        val id = 1L
        val content = InterviewContent().apply {
            interviewContentId = id
            keyword = "AOP"
            category = InterviewCategory.SPRING
            question = "AOP란 무엇인가?"
            modelAnswer = "관점 지향 프로그래밍입니다."
            isHead = true
            hasTail = false
        }

        every { repository.findById(id) } returns Optional.of(content)
        every { repository.countLikesByInterviewContentId(id) } returns 10L

        // when
        val result = service.getInterviewContentById(id)

        // then
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(id)
        assertThat(result.keyword).isEqualTo("AOP")
        assertThat(result.likeCount).isEqualTo(10L)
    }

    @Test
    @DisplayName("면접 질문 ID로 조회 실패")
    fun getInterviewContentById_notFound() {
        // given
        val id = 999L
        every { repository.findById(id) } returns Optional.empty()

        // expect
        val exception = assertThrows<ServiceException> {
            service.getInterviewContentById(id)
        }

        assertThat(exception.message).contains("해당 ID의 면접 질문을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("면접 질문 및 모든 꼬리 질문 조회 성공")
    fun getInterviewContentWithAllTails_success() {
        // given
        val category = InterviewCategory.SPRING

        val main = InterviewContent().apply {
            interviewContentId = 1L
            question = "메인 질문 1"
            modelAnswer = "메인 답변 1"
            hasTail = true
            this.category = category
        }

        val tail1 = InterviewContent().apply {
            interviewContentId = 2L
            question = "꼬리 질문 1"
            modelAnswer = "꼬리 답변 1"
            hasTail = true
            this.category = category
        }

        val tail2 = InterviewContent().apply {
            interviewContentId = 3L
            question = "꼬리 질문 2"
            modelAnswer = "꼬리 답변 2"
            hasTail = false
            this.category = category
        }

        every { repository.findById(1L) } returns Optional.of(main)
        every { repository.countLikesByInterviewContentId(any()) } returns 0L
        every { repository.findRelatedQuestions(1L) } returns listOf(tail1)
        every { repository.findRelatedQuestions(2L) } returns listOf(tail2)

        // when
        val result = service.getInterviewContentWithAllTails(1L)

        // then
        assertThat(result).hasSize(3)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[1].id).isEqualTo(2L)
        assertThat(result[2].id).isEqualTo(3L)
    }

    @Test
    @DisplayName("면접 질문 조회 실패")
    fun getInterviewContentWithAllTails_notFound() {
        // given
        every { repository.findById(999L) } returns Optional.empty()

        // expect
        val exception = assertThrows<ServiceException> {
            service.getInterviewContentWithAllTails(999L)
        }

        assertThat(exception.message).contains("해당 ID의 면접 질문을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("면접 질문 수정 성공")
    fun updateInterviewContent_success() {
        // given
        val id = 1L
        val content = InterviewContent().apply {
            interviewContentId = id
            question = "old"
        }

        every { repository.findById(id) } returns Optional.of(content)
        every { repository.countLikesByInterviewContentId(id) } returns 3L
        every { repository.save(any<InterviewContent>()) } answers { firstArg() }

        val dto = InterviewContentAdminRequestDto(
            InterviewCategory.SPRING,
            "new keyword",
            "new Q",
            "new A",
            null
        )

        // when
        val result = service.updateInterviewContent(id, dto)

        // then
        assertThat(result.question).isEqualTo("new Q")
        assertThat(result.modelAnswer).isEqualTo("new A")
    }

    @Test
    @DisplayName("면접 질문 수정 실패")
    fun updateInterviewContent_notFound() {
        // given
        val id = 999L
        every { repository.findById(id) } returns Optional.empty()

        // expect
        val exception = assertThrows<ServiceException> {
            service.updateInterviewContent(id, InterviewContentAdminRequestDto())
        }

        assertThat(exception.message).contains("해당 ID의 면접 질문을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("면접 질문 및 꼬리 질문 재귀 삭제 성공")
    fun deleteInterviewContentWithAllTails_success() {
        // given
        val head = InterviewContent().apply {
            interviewContentId = 1L
            hasTail = true
        }

        val tail1 = InterviewContent().apply {
            interviewContentId = 2L
            hasTail = false
            headId = 1L
        }

        every { repository.findById(1L) } returns Optional.of(head)
        every { repository.findRelatedQuestions(1L) } returns listOf(tail1)

        // when
        service.deleteInterviewContentWithAllTails(1L)

        // then
        verify { repository.deleteAll(listOf(tail1)) }
        verify { repository.delete(head) }
    }

    @Test
    @DisplayName("면접 질문 삭제 실패 - 존재하지 않는 ID")
    fun deleteInterviewContentWithAllTails_notFound() {
        // given
        every { repository.findById(999L) } returns Optional.empty()

        // when & then
        val exception = assertThrows<ServiceException> {
            service.deleteInterviewContentWithAllTails(999L)
        }

        assertThat(exception.message).contains("해당 ID의 면접 질문을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("꼬리 질문 삭제 시 head 질문의 hasTail 갱신 확인")
    fun deleteInterviewContentWithAllTails_withHeadUpdate() {
        // given
        val tail = InterviewContent().apply {
            interviewContentId = 2L
            hasTail = false
            headId = 1L
        }

        val head = InterviewContent().apply {
            interviewContentId = 1L
            hasTail = true
        }

        every { repository.findById(2L) } returns Optional.of(tail)
        every { repository.findById(1L) } returns Optional.of(head)
        every { repository.findRelatedQuestions(2L) } returns emptyList()
        every { repository.save(any<InterviewContent>()) } answers { firstArg() }

        // when
        service.deleteInterviewContentWithAllTails(2L)

        // then
        verify { repository.save(head) }
        verify { repository.delete(tail) }
    }

    @Test
    @DisplayName("면접 머리 질문 생성 성공")
    fun createInterviewContent_head_success() {
        // given
        val dto = InterviewContentAdminRequestDto(
            InterviewCategory.SPRING,
            "DI",
            "DI란 무엇인가?",
            "DI는 의존성을 외부에서 주입하는 방식입니다.",
            null
        )

        val newContent = InterviewContent().apply {
            interviewContentId = 1L
            category = dto.category!!
            keyword = dto.keyword
            question = dto.question!!
            modelAnswer = dto.modelAnswer!!
            isHead = true
            hasTail = false
        }

        every { repository.save(any()) } returns newContent

        // when
        val result = service.createInterviewContent(dto)

        // then
        assertThat(result.question).isEqualTo(dto.question)
        assertThat(result.modelAnswer).isEqualTo(dto.modelAnswer)
        assertThat(result.category).isEqualTo(dto.category)
        assertThat(result.likeCount).isEqualTo(0L)
    }

    @Test
    @DisplayName("꼬리 질문 생성 성공")
    fun createInterviewContent_tail_success() {
        // given
        val head = InterviewContent.createNewHead("Q1", "A1", InterviewCategory.SPRING, "spring").apply {
            interviewContentId = 10L
        }

        val request = InterviewContentAdminRequestDto().apply {
            headId = 10L
            question = "Q2"
            modelAnswer = "A2"
            keyword = "spring"
            category = InterviewCategory.SPRING
        }

        every { repository.findById(10L) } returns Optional.of(head)
        every { repository.save(any<InterviewContent>()) } answers { firstArg() }

        // when
        val response = service.createInterviewContent(request)

        // then
        assertThat(response.question).isEqualTo("Q2")
        assertThat(response.keyword).isEqualTo("spring")

        verify(exactly = 2) { repository.save(any<InterviewContent>()) }
    }

    @Test
    @DisplayName("중간 질문에 꼬리 질문 추가 실패 - 이미 꼬리 질문이 있는 경우")
    fun createInterviewContent_tail_noEmpty() {
        // given
        val head = InterviewContent().apply {
            interviewContentId = 1L
            hasTail = true
        }

        val dto = InterviewContentAdminRequestDto().apply {
            headId = 1L
            category = InterviewCategory.SPRING
            keyword = "spring"
            question = "중간 질문에 꼬리 추가"
            modelAnswer = "실패해야 함"
        }

        every { repository.findById(1L) } returns Optional.of(head)

        // when & then
        val exception = assertThrows<ServiceException> {
            service.createInterviewContent(dto)
        }

        assertThat(exception.message).contains("중간 질문")
    }

    @Test
    @DisplayName("꼬리 질문 생성 실패 - 존재하지 않는 head ID")
    fun createInterviewContent_tail_notFound() {
        // given
        val dto = InterviewContentAdminRequestDto().apply {
            headId = 999L
            category = InterviewCategory.SPRING
            keyword = "spring"
            question = "Q?"
            modelAnswer = "A"
        }

        every { repository.findById(999L) } returns Optional.empty()

        // when & then
        val exception = assertThrows<ServiceException> {
            service.createInterviewContent(dto)
        }

        assertThat(exception.message).contains("해당 ID의 면접 질문을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("재귀적으로 꼬리 질문 삭제 확인")
    fun deleteInterviewContentWithAllTails_recursive() {
        // given
        val head = InterviewContent().apply {
            interviewContentId = 1L
            hasTail = true
        }

        val tail1 = InterviewContent().apply {
            interviewContentId = 2L
            headId = 1L
            hasTail = true
        }

        val tail2 = InterviewContent().apply {
            interviewContentId = 3L
            headId = 2L
            hasTail = false
        }

        every { repository.findById(1L) } returns Optional.of(head)
        every { repository.findRelatedQuestions(1L) } returns listOf(tail1)
        every { repository.findRelatedQuestions(2L) } returns listOf(tail2)

        val deletedCaptor = slot<List<InterviewContent>>()
        every { repository.deleteAll(capture(deletedCaptor)) } returns Unit
        every { repository.delete(any()) } returns Unit

        // when
        service.deleteInterviewContentWithAllTails(1L)

        // then
        val deleted = deletedCaptor.captured
        assertThat(deleted).containsExactlyInAnyOrder(tail1, tail2)

        verify { repository.delete(head) }
    }
}

