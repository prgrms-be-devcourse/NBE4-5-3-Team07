package com.java.NBE4_5_3_7.domain.interview.repository;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewContentRepository extends JpaRepository<InterviewContent, Long> {
    @Query("select ic.interview_content_id from InterviewContent ic where ic.head = true and ic.head_id is null")
    List<Long> findInterviewContentIdsByHeadTrueAndHeadIdIsNull();

    @Query("select ic.interview_content_id from InterviewContent ic where ic.category = :category and ic.head = true and ic.head_id is null")
    List<Long> findInterviewContentIdsByCategoryAndHeadTrueAndHeadIdIsNull(@Param("category") InterviewCategory category);

    @Query(value = "select distinct keyword from interview_content", nativeQuery = true)
    List<String> findDistinctCategories();

    @Query("select ic.interview_content_id from InterviewContent ic where ic.keyword in :keywords and ic.head = true and ic.head_id is null")
    List<Long> findInterviewKeyword(@Param("keywords") List<String> keywords);

    @Query("SELECT c FROM InterviewContent c WHERE c.interview_content_id > :id ORDER BY c.interview_content_id ASC LIMIT 1")
    Optional<InterviewContent> findNextInterviewContent(@Param("id") Long id);

    boolean existsByQuestion(String question);
}