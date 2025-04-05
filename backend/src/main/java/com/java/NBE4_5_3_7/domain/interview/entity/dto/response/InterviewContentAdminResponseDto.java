package com.java.NBE4_5_3_7.domain.interview.entity.dto.response;

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory;
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent;

public class InterviewContentAdminResponseDto {
    private Long id;
    private Long headId;
    private Long tailId;
    private boolean isHead;
    private boolean hasTail;
    private String keyword;
    private InterviewCategory category;
    private String question;
    private String modelAnswer;
    private Long likeCount;

    public InterviewContentAdminResponseDto() {
    }

    public InterviewContentAdminResponseDto(InterviewContent content, Long likeCount) {
        this.id = content.getInterview_content_id();
        this.headId = content.getHead_id();
        this.tailId = content.getTail_id();
        this.isHead = content.isHead();
        this.hasTail = content.isHasTail();
        this.keyword = content.getKeyword();
        this.category = content.getCategory();
        this.question = content.getQuestion();
        this.modelAnswer = content.getModelAnswer();
        this.likeCount = likeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}