package com.java.NBE4_5_3_7.domain.study

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.Role
import com.java.NBE4_5_3_7.domain.member.entity.SubscriptionPlan
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemo
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.java.NBE4_5_3_7.domain.study.repository.StudyMemoLikeRepository
import com.java.NBE4_5_3_7.domain.study.repository.StudyMemoRepository
import com.java.NBE4_5_3_7.domain.study.service.StudyMemoLikeService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudyMemoLikeServiceTest {
    @Autowired
    lateinit var studyMemoLikeService: StudyMemoLikeService

    @Autowired
    lateinit var studyMemoRepository: StudyMemoRepository

    @Autowired
    lateinit var studyContentRepository: StudyContentRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    lateinit var testMember: Member
    lateinit var testMemo: StudyMemo

    @BeforeEach
    fun setUp() {
        memberRepository.deleteAll()
        testMember = memberRepository.save(
            Member().apply {
                username = "testuser"
                nickname = "테스트유저"
                apiKey = "dummy-api-key"
                profileImgUrl = ""
                role = Role.USER
                subscriptionPlan = SubscriptionPlan.FREE
                subscribeEndDate = LocalDateTime.now().plusDays(30)
            }
        )

        val content = studyContentRepository.save(
            StudyContent().apply {
                title = "Test Content"
                body = "Some study material"
                firstCategory = FirstCategory.Database
                secondCategory = "JPA"
            }
        )

        testMemo = studyMemoRepository.save(
            StudyMemo().apply {
                memoContent = "내용"
                studyContent = content
                member = testMember
                isPublished = true
            }
        )
    }

    @Test
    @DisplayName("좋아요 추가")
    fun addLike() {
        val result = studyMemoLikeService.memoLike(testMemo.id, testMember)
        assertEquals("좋아요 추가", result)

        val likeCount = studyMemoLikeService.getLikeCount(testMemo.id)
        assertEquals(1, likeCount)
    }

    @Test
    @DisplayName("좋아요 취소")
    fun cancelLike() {
        studyMemoLikeService.memoLike(testMemo.id, testMember)

        val result = studyMemoLikeService.memoLike(testMemo.id, testMember)
        assertEquals("좋아요 취소", result)

        val likeCount = studyMemoLikeService.getLikeCount(testMemo.id)
        assertEquals(0, likeCount)
    }

    @Test
    @DisplayName("좋아요 중복 방지")
    fun checkDuplication() {
        studyMemoLikeService.memoLike(testMemo.id, testMember)
        studyMemoLikeService.memoLike(testMemo.id, testMember)// 두 번째는 취소
        studyMemoLikeService.memoLike(testMemo.id, testMember)// 다시 추가

        val likeCount = studyMemoLikeService.getLikeCount(testMemo.id)
        assertEquals(1, likeCount)
    }
}
