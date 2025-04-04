package com.java.NBE4_5_3_7.domain.interview.entity.dto.response;

public class BookmarkResponseDto {
    private Long contentId;
    private String question;
    private String answer;

    public BookmarkResponseDto(Long contentId, String question, String answer) {
        this.contentId = contentId;
        this.question = question;
        this.answer = answer;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}