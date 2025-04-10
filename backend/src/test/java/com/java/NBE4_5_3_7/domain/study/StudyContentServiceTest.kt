package com.java.NBE4_5_3_7.domain.study

import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.java.NBE4_5_3_7.domain.study.service.StudyContentService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudyContentServiceTest {

    @Autowired
    lateinit var studyContentService: StudyContentService

    @Autowired
    lateinit var studyContentRepository: StudyContentRepository

    lateinit var content1: StudyContent
    lateinit var content2: StudyContent

    @BeforeEach
    fun setUp() {
        studyContentRepository.deleteAll()

        content1 = studyContentRepository.save(
            StudyContent().apply {
                title = "JPA란?"
                body = "JPA는 ORM입니다."
                firstCategory = FirstCategory.Web
                secondCategory ="Spring"
            }
        )

        content2 = studyContentRepository.save(
            StudyContent().apply {
                title = "DB 기초"
                body = "RDBMS란?"
                firstCategory = FirstCategory.Database
                secondCategory = "MySQL"
            }
        )
    }

    @Test
    @DisplayName("ID로 콘텐츠를 조회할 수 있다.")
    fun findById() {
        val found = studyContentService.findById(content1.study_content_id!!)
        assertNotNull(found)
        assertEquals("JPA란?", found?.title)
    }

    @Test
    @DisplayName("1차 카테고리를 조회할 수 있다.")
    fun getFirstCategory() {
        val categories = studyContentService.firstCategory
        assertTrue(categories.contains("웹"))
        assertTrue(categories.contains("데이터베이스"))
    }

    @Test
    @DisplayName("1차 카테고리로 2차 카테고리를 조회할 수 있다.")
    fun getSecondCategory() {
        val secondCategories = studyContentService.getSecondCategoryByFirstCategory("Web")
        assertEquals(listOf("Spring"), secondCategories)
    }

    @Test
    @DisplayName("카테고리로 콘텐츠 목록을 페이징 조회할 수 있다.")
    fun getContentsByCategory() {
        val page = studyContentService.getStudyContentsByCategory(
            "Web",
            "Spring",
            PageRequest.of(0, 10)
        )
        assertEquals(1, page.totalElements)
        assertEquals("JPA란?", page.content[0].title)
    }

    @Test
    @DisplayName("콘텐츠 내용을 수정할 수 있다.")
    fun updateContent() {
        studyContentService.updateStudyContent(content1.study_content_id!!, "수정된 내용입니다.")
        val updated = studyContentRepository.findById(content1.study_content_id!!).get()
        assertEquals("수정된 내용입니다.", updated.body)
    }

    @Test
    @DisplayName("콘텐츠를 삭제할 수 있다.")
    fun deleteContent() {
        studyContentService.deleteStudyContent(content1.study_content_id!!)
        val found = studyContentRepository.findById(content1.study_content_id!!)
        assertTrue(found.isEmpty)
    }

    @Test
    @DisplayName("전체 카테고리(1차 → 2차)를 조회할 수 있다.")
    fun getAllCategories() {
        val result = studyContentService.allCategory
        assertTrue(result.containsKey("Web"))
        assertTrue(result.containsKey("Database"))
        assertEquals(listOf("Spring"), result["Web"])
    }
}
