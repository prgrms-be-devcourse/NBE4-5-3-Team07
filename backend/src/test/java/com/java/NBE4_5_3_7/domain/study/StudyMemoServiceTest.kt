package com.java.NBE4_5_3_7.domain.study

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.Role
import com.java.NBE4_5_3_7.domain.member.entity.SubscriptionPlan
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyMemoCreateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyMemoRequestDto
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemo
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.java.NBE4_5_3_7.domain.study.repository.StudyMemoRepository
import com.java.NBE4_5_3_7.domain.study.service.StudyMemoService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudyMemoServiceTest {
    @Autowired
    lateinit var studyMemoService: StudyMemoService

    @Autowired
    lateinit var studyContentRepository: StudyContentRepository

    @Autowired
    lateinit var studyMemoRepository: StudyMemoRepository

    @Autowired
    lateinit var memberRepository: MemberRepository
    lateinit var testMember: Member
    lateinit var testContent: StudyContent

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
        testContent = studyContentRepository.save(
            StudyContent().apply {
                title = "Test Title"
                body = "This is a test"
                firstCategory = FirstCategory.Database
                secondCategory = "JPA"
            }
        )
    }

    @Test
    fun `createStudyMemo should create a new memo`() {
        val dto = StudyMemoCreateRequestDto("메모 내용", true)

        studyMemoService.createStudyMemo(dto, testContent.study_content_id!!, testMember)

        val savedMemo = studyMemoRepository.findByMemberAndStudyContent(testMember, testContent)
        assertNotNull(savedMemo)
        assertEquals("메모 내용", savedMemo?.memoContent)
        assertTrue(savedMemo?.isPublished == true)
    }

    @Test
    fun `getStudyMemoByStudyMemberAndContentId should return memo response`() {
        val memo = studyMemoRepository.save(
            StudyMemo(
                "초기 메모", testContent, testMember, true
            )
        )

        val response = studyMemoService.getStudyMemoByStudyMemberAndContentId(testMember, testContent)

        assertEquals(memo.memoContent, response.memoContent)
    }

    @Test
    fun `updateStudyMemo should update memo correctly`() {
        val memo = studyMemoRepository.save(
            StudyMemo(
                "초기 내용", testContent, testMember, true
            )
        )

        val updateDto = StudyMemoRequestDto("수정된 내용")
        val result = studyMemoService.updateStudyMemo(memo.id, updateDto, testMember)

        assertEquals("수정된 내용", result.memoContent)
    }

    @Test
    fun `deleteStudyMemo should remove the memo`() {
        val memo = studyMemoRepository.save(
            StudyMemo(
                "삭제할 메모", testContent, testMember, true
            )
        )

        studyMemoService.deleteStudyMemo(memo.id, testMember)

        val deleted = studyMemoRepository.findById(memo.id)
        assertTrue(deleted.isEmpty)
    }

    @Test
    fun `getStudyMemoListByStudyContentId should return all memos for content`() {
        studyMemoRepository.save(
            StudyMemo("메모1", testContent, testMember, true)
        )
        studyMemoRepository.save(
            StudyMemo("메모2", testContent, testMember, true)
        )

        val list = studyMemoService.getStudyMemoListByStudyContentId(testContent.study_content_id!!)
        assertEquals(2, list.size)
    }
}
