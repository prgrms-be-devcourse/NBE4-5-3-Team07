package com.java.NBE4_5_3_7.domain.interview.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.KeywordContentRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto
import com.java.NBE4_5_3_7.domain.interview.repository.BookmarkRepository
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentLikeRepository
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import com.java.NBE4_5_3_7.global.exception.ServiceException
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class InterviewServiceTest {

    private lateinit var service: InterviewService

    private val interviewRepository: InterviewContentRepository = mockk()
    private val memberRepository: MemberRepository = mockk()
    private val bookmarkRepository: BookmarkRepository = mockk()
    private val likeRepository: InterviewContentLikeRepository = mockk()

    @BeforeEach
    fun setUp() {
        service = InterviewService(interviewRepository, memberRepository, bookmarkRepository, likeRepository)
    }

    @Test
    @DisplayName("특정 카테고리 헤드 질문 ID 리스트 조회 성공")
    fun categoryHeadQuestion_success() {
        // given
        val category = InterviewCategory.SPRING
        val expectedIds = listOf(1L, 2L)

        every { interviewRepository.findInterviewContentIdsByCategory(category) } returns expectedIds

        // when
        val result = service.categoryHeadQuestion(category)

        // then
        assertThat(result).containsExactly(1L, 2L)
    }

    @Test
    @DisplayName("특정 카테고리 헤드 질문 ID 리스트 조회 - 빈 리스트")
    fun categoryHeadQuestion_emptyList() {
        // given
        val category = InterviewCategory.DATABASE

        every { interviewRepository.findInterviewContentIdsByCategory(category) } returns emptyList()

        // when
        val result = service.categoryHeadQuestion(category)

        // then
        assertThat(result).isNotNull
        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("키워드 목록 조회 성공")
    fun showKeywordList_success() {
        val expectedKeywords = listOf("DI", "JPA", "MVC", "REST")

        every { interviewRepository.findDistinctCategories() } returns expectedKeywords

        val result = service.showKeywordList()

        assertThat(result).isNotNull
        assertThat(result).hasSize(4)
        assertThat(result).containsExactlyElementsOf(expectedKeywords)
    }

    @Test
    @DisplayName("키워드에 따른 헤드 질문 ID 리스트 반환 성공")
    fun keywordHeadQuestion_success() {
        val keywords = listOf("DI", "JPA")
        val dto = KeywordContentRequestDto().apply { keywordList = keywords }
        val expectedIds = listOf(1L, 5L, 9L)

        every { interviewRepository.findInterviewKeyword(keywords) } returns expectedIds

        val result = service.keywordHeadQuestion(dto)

        assertThat(result).isNotNull
        assertThat(result).hasSize(3)
        assertThat(result).containsExactlyElementsOf(expectedIds)
    }

    @Test
    @DisplayName("북마크 추가 성공")
    fun bookmark_add_success() {
        val memberId = 10L
        val contentId = 1L

        val member = mockk<Member>()
        val content = mockk<InterviewContent>()

        every { member.bookmarks } returns mutableListOf()
        every { content.bookmarks } returns mutableListOf()

        every { memberRepository.findById(memberId) } returns Optional.of(member)
        every { interviewRepository.findById(contentId) } returns Optional.of(content)
        every { bookmarkRepository.existsByMemberAndInterviewContent(member, content) } returns false
        every { bookmarkRepository.save(any<InterviewContentBookmark>()) } returns mockk()

        val result = service.bookmark(memberId, contentId)

        assertThat(result).isEqualTo("내 노트에 등록하였습니다.")
        verify { bookmarkRepository.save(any()) }
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    fun bookmark_remove_success() {
        val memberId = 10L
        val contentId = 1L

        val member = mockk<Member>()
        val content = mockk<InterviewContent>()
        val bookmark = mockk<InterviewContentBookmark>()

        every { member.bookmarks } returns mutableListOf()
        every { content.bookmarks } returns mutableListOf()

        every { memberRepository.findById(memberId) } returns Optional.of(member)
        every { interviewRepository.findById(contentId) } returns Optional.of(content)
        every { bookmarkRepository.existsByMemberAndInterviewContent(member, content) } returns true
        every { bookmarkRepository.findByMemberAndInterviewContent(member, content) } returns bookmark
        every { bookmarkRepository.delete(bookmark) } just Runs

        val result = service.bookmark(memberId, contentId)

        assertThat(result).isEqualTo("내 노트에서 삭제하였습니다.")
        verify { bookmarkRepository.delete(bookmark) }
    }

    @Test
    @DisplayName("내 북마크 조회 성공")
    fun showMyBookmark_success() {
        val memberId = 10L

        val content1 = InterviewContent().apply {
            question = "What is DI?"
            modelAnswer = "DI는 의존성 주입입니다."
        }

        val content2 = InterviewContent().apply {
            question = "What is JPA?"
            modelAnswer = "JPA는 자바 ORM 기술입니다."
        }

        val bookmark1 = mockk<InterviewContentBookmark>()
        val bookmark2 = mockk<InterviewContentBookmark>()

        every { bookmark1.id } returns 1L
        every { bookmark1.interviewContent } returns content1
        every { bookmark2.id } returns 2L
        every { bookmark2.interviewContent } returns content2

        val member = mockk<Member>()
        every { member.bookmarks } returns listOf(bookmark1, bookmark2).toMutableList()
        every { memberRepository.findById(memberId) } returns Optional.of(member)

        val result = service.showMyBookmark(memberId)

        assertThat(result).hasSize(2)
        assertThat(result[0].contentId).isEqualTo(1L)
        assertThat(result[0].question).isEqualTo("What is DI?")
        assertThat(result[0].answer).isEqualTo("DI는 의존성 주입입니다.")
        assertThat(result[1].contentId).isEqualTo(2L)
        assertThat(result[1].question).isEqualTo("What is JPA?")
        assertThat(result[1].answer).isEqualTo("JPA는 자바 ORM 기술입니다.")
    }

    @Test
    @DisplayName("내 북마크 조회 - 북마크 없음")
    fun showMyBookmark_emptyList() {
        val memberId = 10L
        val member = mockk<Member>()

        every { member.bookmarks } returns emptyList<InterviewContentBookmark>().toMutableList()
        every { memberRepository.findById(memberId) } returns Optional.of(member)

        val result = service.showMyBookmark(memberId)

        assertThat(result).isEmpty()
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    fun deleteNote_success() {
        val noteId = 1L
        val member = mockk<Member>()
        val bookmark = mockk<InterviewContentBookmark>()

        every { bookmark.member } returns member
        every { bookmarkRepository.findById(noteId) } returns Optional.of(bookmark)
        every { bookmarkRepository.deleteById(noteId) } just Runs

        service.deleteNote(noteId, member)

        verify { bookmarkRepository.deleteById(noteId) }
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 북마크 없음")
    fun deleteNote_notFound() {
        val noteId = 999L
        val member = mockk<Member>()

        every { bookmarkRepository.findById(noteId) } returns Optional.empty()

        assertThatThrownBy { service.deleteNote(noteId, member) }
            .isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("북마크를 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 권한 없음")
    fun deleteNote_unauthorized() {
        val noteId = 1L
        val member1 = mockk<Member>()
        val member2 = mockk<Member>()
        val bookmark = mockk<InterviewContentBookmark>()

        every { bookmark.member } returns member2
        every { bookmarkRepository.findById(noteId) } returns Optional.of(bookmark)

        assertThatThrownBy { service.deleteNote(noteId, member1) }
            .isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("본인이 추가한 북마크만 삭제할 수 있습니다.")
    }
}