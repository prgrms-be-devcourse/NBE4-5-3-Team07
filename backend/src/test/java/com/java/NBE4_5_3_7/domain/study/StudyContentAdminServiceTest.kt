package com.java.NBE4_5_3_7.domain.study

import com.fasterxml.jackson.databind.ObjectMapper
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentCreateRequestDto
import com.java.NBE4_5_3_7.domain.study.dto.request.StudyContentUpdateRequestDto
import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.java.NBE4_5_3_7.domain.study.service.StudyContentAdminService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class StudyContentAdminServiceTest {

    @Autowired
    lateinit var studyContentAdminService: StudyContentAdminService

    @Autowired
    lateinit var studyContentRepository: StudyContentRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `createStudyContent - 정상 생성`() {
        // given
        val request = StudyContentCreateRequestDto().apply {
            firstCategory = "ComputerArchitecture"
            secondCategory = "자료구조"
            title = "스택과 큐"
            body = "스택은 LIFO, 큐는 FIFO입니다."
        }

        // when
        val result = studyContentAdminService.createStudyContent(request)

        // then
        assertEquals("스택과 큐", result.title)
        assertEquals("자료구조", result.secondCategory)
        assertEquals("스택은 LIFO, 큐는 FIFO입니다.", result.body)
    }

    @Test
    fun `getStudyContentById - 존재하는 콘텐츠 조회`() {
        // given
        val saved = studyContentRepository.save(
            StudyContent(
                firstCategory = FirstCategory.Network,
                secondCategory = "네트워크",
                title = "OSI 7계층",
                body = "물리계층부터 응용계층까지"
            )
        )

        // when
        val result = saved.study_content_id?.let { studyContentAdminService.getStudyContentById(it) }

        // then
        assertEquals("OSI 7계층", result?.title)
    }

    @Test
    fun `updateStudyContent - 타이틀 변경`() {
        // given
        val saved = studyContentRepository.save(
            StudyContent(
                firstCategory = FirstCategory.OperatingSystem,
                secondCategory = "운영체제",
                title = "프로세스",
                body = "PCB란?"
            )
        )

        val updateRequest = StudyContentUpdateRequestDto(
            title = "스레드",
            firstCategory = "운영체제" ,
            secondCategory = FirstCategory.OperatingSystem.toString(),
            updateContent = "스레드란?"
        )

        // when
        saved.study_content_id?.let { studyContentAdminService.updateStudyContent(it, updateRequest) }

        // then
        val updated = saved.study_content_id?.let { studyContentRepository.findById(it).get() }
        assertEquals("스레드", updated?.title)
        assertEquals("스레드란?", updated?.body)
    }

    @Test
    fun `deleteStudyContent - 삭제 확인`() {
        // given
        val saved = studyContentRepository.save(
            StudyContent(
                firstCategory = FirstCategory.ComputerArchitecture,
                secondCategory = "알고리즘",
                title = "정렬",
                body = "퀵정렬, 병합정렬"
            )
        )

        // when
        saved.study_content_id?.let { studyContentAdminService.deleteStudyContent(it) }

        // then
        val exists = saved.study_content_id?.let { studyContentRepository.existsById(it) }
        exists?.let { assertFalse(it) }
    }
}
