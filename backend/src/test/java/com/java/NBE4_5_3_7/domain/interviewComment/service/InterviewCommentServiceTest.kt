package com.java.NBE4_5_3_7.domain.interviewComment.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.java.NBE4_5_3_7.domain.interviewComment.dto.request.InterviewCommentRequestDto
import com.java.NBE4_5_3_7.domain.interviewComment.entity.InterviewContentComment
import com.java.NBE4_5_3_7.domain.interviewComment.repository.InterviewCommentRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.global.exception.ServiceException
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class InterviewCommentServiceTest {

    private lateinit var service: InterviewCommentService
    private val commentRepository: InterviewCommentRepository = mockk()
    private val contentRepository: InterviewContentRepository = mockk()

    @BeforeEach
    fun setUp() {
        service = InterviewCommentService(commentRepository, contentRepository)
    }

    @Test
    @DisplayName("댓글 생성 성공")
    fun createComment_success() {
        // given
        val member = Member().apply { id = 1L }

        val content = InterviewContent().apply {
            interviewContentId = 1L
            question = "Spring에서 의존성 주입(DI)이란?"
            modelAnswer = "DI는 객체 간의 의존 관계를 외부에서 설정해주는 방법입니다."
            category = InterviewCategory.SPRING
        }

        val request = InterviewCommentRequestDto(
            comment = "깔끔한 설명 감사합니다.",
            isPublic = true,
            interviewContentId = 1L
        )

        val savedComment = InterviewContentComment(
            commentId = 123L,
            answer = request.comment,
            isPublic = request.isPublic,
            interviewContent = content,
            member = member
        )

        every { contentRepository.findById(1L) } returns Optional.of(content)
        every { commentRepository.save(any()) } returns savedComment

        // when
        val result = service.createComment(request, member)

        // then
        assertThat(result.commentId).isEqualTo(123L)
        assertThat(result.comment).isEqualTo("깔끔한 설명 감사합니다.")
        assertThat(result.isPublic).isTrue()
        assertThat(result.interviewContentId).isEqualTo(1L)
        assertThat(result.interviewContentTitle).isEqualTo("Spring에서 의존성 주입(DI)이란?")
    }

    @Test
    @DisplayName("댓글 생성 실패 - 인터뷰 콘텐츠 없음")
    fun createComment_contentNotFound() {
        // given
        val member = Member().apply { id = 1L }
        val request = InterviewCommentRequestDto("테스트 댓글", true, interviewContentId = 999L)

        every { contentRepository.findById(999L) } returns Optional.empty()

        // when & then
        assertThatThrownBy { service.createComment(request, member) }
            .isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("해당 인터뷰 콘텐츠를 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("댓글 수정 성공 - 작성자가 본인인 경우")
    fun updateComment_success() {
        // given
        val member = Member().apply { id = 1L }

        val content = InterviewContent().apply {
            interviewContentId = 100L
            question = "Spring DI란?"
            modelAnswer = "의존성 주입입니다."
            category = InterviewCategory.SPRING
        }

        val existingComment = InterviewContentComment(
            commentId = 10L,
            answer = "기존 댓글",
            isPublic = false,
            interviewContent = content,
            member = member  // 본인이 작성한 댓글
        )

        val updateDto = InterviewCommentRequestDto("수정된 댓글입니다", true, 100L)

        every { commentRepository.findById(10L) } returns Optional.of(existingComment)

        // when
        val result = service.updateComment(10L, updateDto, member)

        // then
        assertThat(result.commentId).isEqualTo(10L)
        assertThat(result.comment).isEqualTo("수정된 댓글입니다")
        assertThat(result.isPublic).isTrue()
        assertThat(result.interviewContentId).isEqualTo(100L)
        assertThat(result.interviewContentTitle).isEqualTo("Spring DI란?")
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자가 아님")
    fun updateComment_notOwner() {
        // given
        val member = Member().apply { id = 1L }
        val other = Member().apply { id = 2L }
        val content = InterviewContent().apply {
            interviewContentId = 2L
            question = "Q"
            modelAnswer = "A"
            category = InterviewCategory.SPRING
        }
        val comment = InterviewContentComment(
            commentId = 5L,
            answer = "이전 답변",
            isPublic = false,
            interviewContent = content,
            member = other
        )

        every { commentRepository.findById(5L) } returns Optional.of(comment)

        // then
        assertThatThrownBy {
            service.updateComment(5L, InterviewCommentRequestDto("수정된 댓글", true, 2L), member)
        }.isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("본인이 작성한 댓글만 수정할 수 있습니다.")
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 없음")
    fun updateComment_notFound() {
        // given
        val member = Member().apply { id = 1L }
        val dto = InterviewCommentRequestDto("수정", true, 1L)

        every { commentRepository.findById(100L) } returns Optional.empty()

        // when & then
        assertThatThrownBy { service.updateComment(100L, dto, member) }
            .isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("해당 댓글을 찾을 수 없습니다.")
    }


    @Test
    @DisplayName("댓글 삭제 성공")
    fun deleteComment_success() {
        // given
        val member = Member().apply { id = 1L }
        val content = InterviewContent().apply {
            interviewContentId = 2L
            question = "Q"
            modelAnswer = "A"
            category = InterviewCategory.SPRING
        }
        val comment = InterviewContentComment(
            commentId = 10L,
            answer = "삭제할 댓글",
            isPublic = false,
            interviewContent = content,
            member = member
        )

        every { commentRepository.findById(10L) } returns Optional.of(comment)
        every { commentRepository.deleteById(10L) } just Runs

        // when
        service.deleteComment(10L, member)

        // then
        verify { commentRepository.deleteById(10L) }
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    fun deleteComment_notFound() {
        // given
        val member = Member().apply { id = 1L }

        every { commentRepository.findById(100L) } returns Optional.empty()

        // when & then
        assertThatThrownBy { service.deleteComment(100L, member) }
            .isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("댓글을 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 작성자가 아님")
    fun deleteComment_notOwner() {
        // given
        val writer = Member().apply { id = 1L }
        val stranger = Member().apply { id = 2L }

        val content = InterviewContent().apply {
            interviewContentId = 10L
            question = "DI란?"
            modelAnswer = "의존성 주입"
            category = InterviewCategory.SPRING
        }

        val comment = InterviewContentComment(
            commentId = 5L,
            answer = "내 댓글",
            isPublic = true,
            interviewContent = content,
            member = writer
        )

        every { commentRepository.findById(5L) } returns Optional.of(comment)

        // when & then
        assertThatThrownBy { service.deleteComment(5L, stranger) }
            .isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("본인이 작성한 댓글만 삭제할 수 있습니다.")
    }


    @Test
    @DisplayName("내 댓글 조회 성공 - 내 댓글만 필터링")
    fun getMyComments_success() {
        // given
        val member = Member().apply { id = 1L }
        val other = Member().apply { id = 2L }

        val content = InterviewContent().apply {
            interviewContentId = 100L
            question = "JPA의 장점은?"
            modelAnswer = "생산성, 유지보수성 등"
            category = InterviewCategory.SPRING
        }

        val myComment = InterviewContentComment(
            commentId = 1L,
            answer = "JPA 덕분에 코드가 깔끔해졌어요.",
            isPublic = true,
            interviewContent = content,
            member = member
        )

        val othersComment = InterviewContentComment(
            commentId = 2L,
            answer = "동의합니다.",
            isPublic = true,
            interviewContent = content,
            member = other
        )

        every { commentRepository.findByInterviewContentId(100L) } returns listOf(myComment, othersComment)

        // when
        val result = service.getMyComments(100L, member)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].commentId).isEqualTo(1L)
        assertThat(result[0].comment).isEqualTo("JPA 덕분에 코드가 깔끔해졌어요.")
    }

    @Test
    @DisplayName("공개 댓글 조회 성공 - 나 제외 + 공개 댓글만 필터링")
    fun getPublicComments_success() {
        // given
        val member = Member().apply { id = 1L }
        val other = Member().apply { id = 2L }

        val content = InterviewContent().apply {
            interviewContentId = 100L
            question = "REST API의 특징은?"
            modelAnswer = "무상태성, 자원 기반 설계 등"
            category = InterviewCategory.NETWORK
        }

        val publicFromOther = InterviewContentComment(
            commentId = 10L,
            answer = "자원 중심이라 이해하기 쉬워요.",
            isPublic = true,
            interviewContent = content,
            member = other
        )

        val privateFromOther = InterviewContentComment(
            commentId = 11L,
            answer = "이건 비공개예요.",
            isPublic = false,
            interviewContent = content,
            member = other
        )

        val myComment = InterviewContentComment(
            commentId = 12L,
            answer = "좋은 설명입니다.",
            isPublic = true,
            interviewContent = content,
            member = member
        )

        every { commentRepository.findByInterviewContentId(100L) } returns listOf(
            publicFromOther,
            privateFromOther,
            myComment
        )

        // when
        val result = service.getPublicComments(100L, member)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].commentId).isEqualTo(10L)
        assertThat(result[0].comment).isEqualTo("자원 중심이라 이해하기 쉬워요.")
    }

    @Test
    @DisplayName("멤버와 카테고리로 댓글 조회 성공")
    fun getCommentsByMemberAndCategory_success() {
        // given
        val member = Member().apply { id = 1L }

        val content1 = InterviewContent().apply {
            interviewContentId = 1L
            question = "트랜잭션이란?"
            modelAnswer = "데이터의 논리적 작업 단위"
            category = InterviewCategory.DATABASE
        }

        val content2 = InterviewContent().apply {
            interviewContentId = 2L
            question = "정규화란?"
            modelAnswer = "데이터 중복을 제거하는 과정"
            category = InterviewCategory.DATABASE
        }

        val comment1 = InterviewContentComment(
            commentId = 101L,
            answer = "정확한 정의네요.",
            isPublic = true,
            interviewContent = content1,
            member = member
        )

        val comment2 = InterviewContentComment(
            commentId = 102L,
            answer = "3NF까지는 꼭 기억하자.",
            isPublic = false,
            interviewContent = content2,
            member = member
        )

        every {
            commentRepository.findByMemberAndInterviewContentCategory(member, InterviewCategory.DATABASE)
        } returns listOf(comment1, comment2)

        // when
        val result = service.getCommentsByMemberAndCategory(member, InterviewCategory.DATABASE)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].commentId).isEqualTo(101L)
        assertThat(result[1].commentId).isEqualTo(102L)
    }
}