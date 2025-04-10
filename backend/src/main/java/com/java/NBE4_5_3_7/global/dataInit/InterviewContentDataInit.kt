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
        val contentList = mutableListOf<InterviewContent>()

        try {
            val csvFile = File("data/기술면접컨텐츠데이터-종합.csv")
            val streamReader = InputStreamReader(FileInputStream(csvFile), StandardCharsets.UTF_8)
            val csvReader = CSVReader(streamReader)

            var isFirstLine = true
            var line: Array<String>?

            while (csvReader.readNext().also { line = it } != null) {
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                line?.let { currentLine ->
                    val headIdStr = currentLine[1]
                    val headId = if (!"NULL".equals(headIdStr, ignoreCase = true)) headIdStr.toLong() else null

                    val category = when (currentLine[2].lowercase()) {
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

                    if (repository.existsByQuestion(question)) {
                        return@let
                    }

                    if (isHead && headId != null) {
                        throw IllegalStateException("isHead=true인데 headId가 존재합니다. 질문: [$question]")
                    }

                    val content = InterviewContent().apply {
                        this.headId = headId
                        this.category = category!!
                        this.keyword = keyword
                        this.question = question
                        this.modelAnswer = modelAnswer
                        this.isHead = isHead
                    }

                    contentList.add(content)
                }
            }
            csvReader.close()

            repository.saveAll(contentList)

        } catch (e: Exception) {
            throw RuntimeException("CSV 데이터를 DB에 저장하는데 실패했습니다.", e)
        }
    }

    @Transactional
    fun updateHasTailField() {
        val contents = repository.findAll()
            .associateBy { it.interviewContentId!! }

        // headId → 꼬리 리스트
        val headToTails = contents.values
            .filter { it.headId != null }
            .groupBy { it.headId!! }
            .mapValues { entry -> entry.value.sortedBy { it.interviewContentId } }

        headToTails.forEach { (headId, tails) ->
            val head = contents[headId]
            val firstTail = tails.firstOrNull()

            if (head != null && firstTail != null) {
                head.hasTail = true
                head.tailId = firstTail.interviewContentId
                repository.save(head)
            }

            // 꼬리들 사이도 순차 연결
            tails.zipWithNext { a, b ->
                a.hasTail = true
                a.tailId = b.interviewContentId
                repository.save(a)
            }

            // 마지막 꼬리 처리
            val last = tails.lastOrNull()
            if (last != null) {
                last.hasTail = false
                last.tailId = null
                repository.save(last)
            }
        }
    }
}