package com.java.NBE4_5_3_7.global.dataInit

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.opencsv.CSVReader
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
@Transactional
class InterviewContentDataInit(private val repository: InterviewContentRepository) {

    @PostConstruct
    fun dataInit() {
        if (repository.count() == 0L) {
            importCsvData()
            updateHasTailField()
        } else {
            println("초기 질문 데이터가 이미 존재")
        }
    }

    fun importCsvData() {
        val headData = ArrayList<InterviewContent>()
        val tailData = ArrayList<InterviewContent>()

        try {
            // resources 폴더에 위치한 CSV 파일을 불러옴
            val csvFile = File("data/기술면접컨텐츠데이터-종합.csv")
            val streamReader = InputStreamReader(FileInputStream(csvFile), StandardCharsets.UTF_8)
            val csvReader = CSVReader(streamReader)

            var isFirstLine = true
            var line: Array<String>?

            while (csvReader.readNext().also { line = it } != null) {
                // 첫 번째 라인은 헤더이므로 건너뜀
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                line?.let { currentLine ->
                    val headIdStr = currentLine[1]
                    val headId = if (!"NULL".equals(headIdStr, ignoreCase = true)) headIdStr.toLong() else null

                    val category = when (currentLine[2].toLowerCase()) {
                        "database" -> InterviewCategory.DATABASE
                        "network" -> InterviewCategory.NETWORK
                        "operatingsystem" -> InterviewCategory.OperatingSystem
                        "spring" -> InterviewCategory.SPRING
                        else -> {
                            println("category 설정 오류로 인해 null 값 적용.")
                            null
                        }
                    }

                    val keyword = currentLine[3]
                    val question = currentLine[4]
                    val modelAnswer = currentLine[5]
                    val isHead = "TRUE".equals(currentLine[6], ignoreCase = true)

                    // 중복 방지
                    if (repository.existsByQuestion(question)) {
                        return@let
                    }

                    val content = InterviewContent().apply {
                        this.headId = headId
                        this.category = category!!
                        this.keyword = keyword
                        this.question = question
                        this.modelAnswer = modelAnswer
                        this.isHead = isHead
                    }

                    if (isHead) {
                        headData.add(content)
                    } else {
                        tailData.add(content)
                    }
                }
            }
            csvReader.close()

            repository.saveAll(headData)
            repository.saveAll(tailData)

        } catch (e: Exception) {
            throw RuntimeException("CSV 데이터를 DB에 저장하는데 실패했습니다.", e)
        }
    }

    @Transactional
    fun updateHasTailField() {
        repository.findAll().stream()
            .filter { interview -> interview.headId != null }
            .forEach { tail ->
                tail.headId?.let { headId ->
                    repository.findById(headId).ifPresent { head ->
                        head.hasTail = true
                        head.tailId = tail.interviewContentId
                        repository.save(head)
                    }
                }
            }
    }
}