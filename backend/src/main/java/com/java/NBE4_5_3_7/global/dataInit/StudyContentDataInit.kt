package com.java.NBE4_5_3_7.global.dataInit

import com.java.NBE4_5_3_7.domain.study.entity.FirstCategory
import com.java.NBE4_5_3_7.domain.study.entity.StudyContent
import com.java.NBE4_5_3_7.domain.study.repository.StudyContentRepository
import com.opencsv.CSVReader
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class StudyContentDataInit(private val repository: StudyContentRepository) {

    @PostConstruct
    fun importCsvData() {
        try {
            // 프로젝트 상위 디렉토리의 data 폴더 내 CSV 파일 경로 (파일명은 실제 사용 파일명으로 수정)
            val csvFile = File("data/학습컨텐츠데이터-종합.csv")
            val streamReader = InputStreamReader(FileInputStream(csvFile), StandardCharsets.UTF_8)
            val csvReader = CSVReader(streamReader)

            var isFirstLine = true
            var line: Array<String>?

            while (csvReader.readNext().also { line = it } != null) {
                // 첫 줄은 헤더이므로 건너뜁니다.
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                line?.let { currentLine ->
                    // CSV 컬럼 순서: study_content_id, first_category, second_category, title, body
                    // study_content_id는 auto-generated 필드이므로 사용하지 않습니다.
                    val firstCategoryStr = currentLine[1].trim()
                    val firstCategory = when (firstCategoryStr) {
                        "Computer Architecture", "컴퓨터구조" -> FirstCategory.ComputerArchitecture
                        "Data Structure", "자료구조" -> FirstCategory.DataStructure
                        "Database", "데이터베이스" -> FirstCategory.Database
                        "NetWork", "네트워크" -> FirstCategory.Network
                        "Operating System", "운영체제" -> FirstCategory.OperatingSystem
                        "SoftWare Engineering", "소프트웨어엔지니어링" -> FirstCategory.SoftwareEngineering
                        "Web", "웹" -> FirstCategory.Web
                        else -> FirstCategory.Database  // 기본값으로 Database 할당 (필요시 수정)
                    }

                    val secondCategory = currentLine[2].trim()
                    val title = currentLine[3].trim()
                    val body = currentLine[4].trim()

                    // 중복된 데이터가 있으면 저장하지 않음
                    if (repository.findByTitle(title)!!.isPresent) {
                        return@let
                    }

                    val studyContent = StudyContent().apply {
                        this.firstCategory = firstCategory
                        this.secondCategory = secondCategory
                        this.title = title
                        this.body = body
                    }

                    repository.save(studyContent)
                }
            }
            csvReader.close()
        } catch (e: Exception) {
            throw RuntimeException("StudyContent CSV 데이터를 DB에 저장하는데 실패했습니다.", e)
        }
    }
}