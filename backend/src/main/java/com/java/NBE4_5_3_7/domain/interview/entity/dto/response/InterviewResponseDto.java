package com.java.NBE4_5_3_7.domain.interview.entity.dto.response;

public class InterviewResponseDto {
    private Long id;
    private Long head_id;
    private Long tail_id;
    private String question;
    private String model_answer;
    private String category;
    private String keyword;
    private Long next_id;
    private Long likeCount;
    private boolean likedByUser;

    public InterviewResponseDto(
            Long id,
            Long head_id,
            Long tail_id,
            String question,
            String model_answer,
            String category,
            String keyword,
            Long next_id,
            Long likeCount,
            boolean likedByUser
    ) {
        this.id = id;
        this.head_id = head_id;
        this.tail_id = tail_id;
        this.question = question;
        this.model_answer = model_answer;
        this.category = category;
        this.keyword = keyword;
        this.next_id = next_id;
        this.likeCount = likeCount;
        this.likedByUser = likedByUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHead_id() {
        return head_id;
    }

    public void setHead_id(Long head_id) {
        this.head_id = head_id;
    }

    public Long getTail_id() {
        return tail_id;
    }

    public void setTail_id(Long tail_id) {
        this.tail_id = tail_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getModel_answer() {
        return model_answer;
    }

    public void setModel_answer(String model_answer) {
        this.model_answer = model_answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getNext_id() {
        return next_id;
    }

    public void setNext_id(Long next_id) {
        this.next_id = next_id;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }
}