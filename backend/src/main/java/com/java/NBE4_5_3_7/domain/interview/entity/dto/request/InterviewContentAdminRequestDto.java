package com.java.NBE4_5_3_7.domain.interview.entity.dto.request;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;

public class InterviewContentAdminRequestDto {
    private Long headId;
    private Long tailId;
    private boolean isHead;
    private boolean hasTail;
    private String keyword;
    private InterviewCategory category;
    private String question;
    private String modelAnswer;

    public InterviewContentAdminRequestDto() {
    }

    public Long getHeadId() {
        return headId;
    }

    public void setHeadId(Long headId) {
        this.headId = headId;
    }

    public Long getTailId() {
        return tailId;
    }

    public void setTailId(Long tailId) {
        this.tailId = tailId;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public boolean isHasTail() {
        return hasTail;
    }

    public void setHasTail(boolean hasTail) {
        this.hasTail = hasTail;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public InterviewCategory getCategory() {
        return category;
    }

    public void setCategory(InterviewCategory category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public void setModelAnswer(String modelAnswer) {
        this.modelAnswer = modelAnswer;
    }
}