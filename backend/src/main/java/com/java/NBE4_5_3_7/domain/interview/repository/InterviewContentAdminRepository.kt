package com.java.NBE4_5_3_7.domain.interview.repository;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterviewContentAdminRepository extends JpaRepository<InterviewContent, Long> {
    // 카테고리 목록 조회
    @Query("select distinct ic.category from InterviewContent ic")
    List<InterviewCategory> findUniqueCategories();

    // 특정 카테고리의 키워드 목록 조회
    @Query("select distinct ic.keyword from InterviewContent ic where ic.category = :category")
    List<String> findUniqueKeywordsByCategory(@Param("category") InterviewCategory category);

    // 특정 카테고리에 속한 모든 면접 질문 조회
    @Query("select ic from InterviewContent ic where ic.category = :category")
    Page<InterviewContent> findByCategory(@Param("category") InterviewCategory category, Pageable pageable);

    // 특정 질문의 좋아요 개수 조회
    @Query("SELECT COUNT(icl) FROM InterviewContentLike icl WHERE icl.interviewContent.interview_content_id = :interviewContentId")
    Long countLikesByInterviewContentId(@Param("interviewContentId") Long interviewContentId);

    // 특정 카테고리 & 키워드를 포함하는 질문 조회
    @Query("SELECT ic FROM InterviewContent ic WHERE ic.category = :category AND ic.keyword = :keyword")
    Page<InterviewContent> findByCategoryAndKeyword(@Param("category") InterviewCategory category, @Param("keyword") String keyword, Pageable pageable);

    // 주어진 head_id를 기준으로 해당 질문과 연결된 모든 후속(꼬리) 질문을 조회
    @Query("SELECT ic FROM InterviewContent ic WHERE ic.head_id = :id")
    List<InterviewContent> findRelatedQuestions(@Param("id") Long id);
}
