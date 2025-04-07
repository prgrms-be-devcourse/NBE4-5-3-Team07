package com.java.NBE4_5_3_7.domain.interview.service

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentLike
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentLikeRepository
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.util.*
import java.util.concurrent.TimeUnit

class InterviewLikeServiceTest {

    private lateinit var service: InterviewLikeService

    private val interviewContentRepository: InterviewContentRepository = mockk()
    private val memberRepository: MemberRepository = mockk()
    private val interviewContentLikeRepository: InterviewContentLikeRepository = mockk()
    private val redissonClient: RedissonClient = mockk()
    private val rLock: RLock = mockk()

    @BeforeEach
    fun setup() {
        service = InterviewLikeService(
            interviewContentLikeRepository,
            interviewContentRepository,
            memberRepository,
            redissonClient
        )
    }

    @Test
    @DisplayName("좋아요 추가 성공")
    fun interviewLike_add_success() {
        // given
        val content = InterviewContent().apply { interviewContentId = 1L }
        val member = mockk<Member>()

        every { redissonClient.getLock(any()) } returns rLock
        every { rLock.tryLock(5L, 10L, TimeUnit.SECONDS) } returns true
        every { rLock.isHeldByCurrentThread } returns true
        every { interviewContentRepository.findById(1L) } returns Optional.of(content)
        every { memberRepository.findById(1L) } returns Optional.of(member)
        every {
            interviewContentLikeRepository.findByInterviewContentAndMember(content, member)
        } returns Optional.empty()
        every { interviewContentLikeRepository.save(any<InterviewContentLike>()) } returns mockk()
        every { rLock.unlock() } just Runs

        // when
        val result = service.interviewLike(1L, 1L)

        // then
        assertThat(result).isEqualTo("좋아요 추가")
        verify { interviewContentLikeRepository.save(any()) }
        verify { rLock.unlock() }
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    fun interviewLike_cancel_success() {
        // given
        val content = InterviewContent().apply { interviewContentId = 1L }
        val member = mockk<Member>()
        val like = InterviewContentLike(content, member)

        every { redissonClient.getLock(any()) } returns rLock
        every { rLock.tryLock(5L, 10L, TimeUnit.SECONDS) } returns true
        every { rLock.isHeldByCurrentThread } returns true
        every { interviewContentRepository.findById(1L) } returns Optional.of(content)
        every { memberRepository.findById(1L) } returns Optional.of(member)
        every {
            interviewContentLikeRepository.findByInterviewContentAndMember(content, member)
        } returns Optional.of(like)
        every { interviewContentLikeRepository.delete(like) } just Runs
        every { rLock.unlock() } just Runs

        // when
        val result = service.interviewLike(1L, 1L)

        // then
        assertThat(result).isEqualTo("좋아요 취소")
        verify { interviewContentLikeRepository.delete(like) }
        verify { rLock.unlock() }
    }
}