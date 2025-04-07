package com.java.NBE4_5_3_7.global.dataInit;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InterviewContentDataInit {
    private final InterviewContentRepository repository;

    @PostConstruct
    public void dataInit() {
        if (repository.count() == 0) {
            importCsvData();
            updateHasTailField();
        } else {
            System.out.println("초기 질문 데이터가 이미 존재");
        }
    }

    public void importCsvData() {
        List<InterviewContent> headData = new ArrayList<>();
        List<InterviewContent> tailData = new ArrayList<>();

        try {
            // resources 폴더에 위치한 CSV 파일을 불러옴
            File csvFile = new File("data/기술면접컨텐츠데이터-종합.csv");
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8);
            CSVReader csvReader = new CSVReader(streamReader);

            String[] line;
            boolean isFirstLine = true;
            while ((line = csvReader.readNext()) != null) {
                // 첫 번째 라인은 헤더이므로 건너뜀
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String headIdStr = line[1];
                Long headId = !"NULL".equalsIgnoreCase(headIdStr) ? Long.parseLong(headIdStr) : null;

                InterviewCategory category;
                switch (line[2].toLowerCase()) {
                    case "database" -> category = InterviewCategory.DATABASE;
                    case "network" -> category = InterviewCategory.NETWORK;
                    case "operatingsystem" -> category = InterviewCategory.OperatingSystem;
                    case "spring" -> category = InterviewCategory.SPRING;
                    default -> {
                        category = null;
                        System.out.println("category 설정 오류로 인해 null 값 적용.");
                    }
                }

                String keyword = line[3];
                String question = line[4];
                String modelAnswer = line[5];
                boolean isHead = "TRUE".equalsIgnoreCase(line[6]);

                // 중복 방지
                if (repository.existsByQuestion(question)) {
                    continue;
                }

                InterviewContent content = new InterviewContent();
                content.setHeadId(headId);
                content.setCategory(category);
                content.setKeyword(keyword);
                content.setQuestion(question);
                content.setModelAnswer(modelAnswer);
                content.setHead(isHead);

                if (isHead) {
                    headData.add(content);
                } else {
                    tailData.add(content);
                }
            }
            csvReader.close();

            repository.saveAll(headData);
            repository.saveAll(tailData);

        } catch (Exception e) {
            throw new RuntimeException("CSV 데이터를 DB에 저장하는데 실패했습니다.", e);
        }
    }

    @Transactional
    public void updateHasTailField() {
        repository.findAll().stream()
                .filter(interview -> interview.getHeadId() != null)
                .forEach(tail -> {
                    repository.findById(tail.getHeadId()).ifPresent(head -> {
                        head.setHasTail(true);
                        head.setTailId(tail.getInterviewContentId());
                        repository.save(head);
                    });
                });
    }
}