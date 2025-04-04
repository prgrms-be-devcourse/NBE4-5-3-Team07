package com.java.NBE4_5_3_7.domain.interview.entity;

import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class InterviewContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interview_content_id;

    private Long head_id;

    private Long tail_id;

    @Enumerated(EnumType.STRING)
    private InterviewCategory category;

    private String keyword;

    @Lob
    private String question;

    @Lob
    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(name = "is_head")
    private boolean head;

    @Column(name = "has_tail")
    private boolean hasTail;

    @OneToMany(mappedBy = "interviewContent", cascade = CascadeType.ALL)
    private List<InterviewContentBookmark> bookmarks = new ArrayList<>();

    public InterviewContent() {
    }

    public Long getInterview_content_id() {
        return interview_content_id;
    }

    public Long getHead_id() {
        return head_id;
    }

    public Long getTail_id() {
        return tail_id;
    }

    public String getQuestion() {
        return question;
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public InterviewCategory getCategory() {
        return category;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isHead() {
        return head;
    }

    public boolean isHasTail() {
        return hasTail;
    }

    public List<InterviewContentBookmark> getBookmarks() {
        return bookmarks;
    }

    public void setCategory(InterviewCategory category) {
        this.category = category;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setModelAnswer(String modelAnswer) {
        this.modelAnswer = modelAnswer;
    }


    public void setHead_id(Long head_id) {
        this.head_id = head_id;
    }

    public void setHead(boolean head) {
        this.head = head;
    }

    public void setHasTail(boolean hasTail) {
        this.hasTail = hasTail;
    }

    public void setTail_id(Long tail_id) {
        this.tail_id = tail_id;
    }

    public static InterviewContent createNewHead(String question, String modelAnswer, InterviewCategory category, String keyword) {
        InterviewContent newContent = new InterviewContent();
        newContent.question = question;
        newContent.modelAnswer = modelAnswer;
        newContent.category = category;
        newContent.keyword = keyword;
        newContent.head = true;
        newContent.hasTail = false;
        return newContent;
    }

    public static InterviewContent createTail(InterviewContent headContent, InterviewContentAdminRequestDto requestDto) {
        if (headContent.hasTail) {
            throw new IllegalStateException("이미 꼬리 질문이 존재하는 질문입니다.");
        }

        InterviewContent newContent = new InterviewContent();
        newContent.question = requestDto.getQuestion();
        newContent.modelAnswer = requestDto.getModelAnswer();
        newContent.category = headContent.category;
        newContent.keyword = requestDto.getKeyword();
        newContent.head_id = headContent.interview_content_id;
        newContent.head = false;
        newContent.hasTail = false;

        headContent.tail_id = newContent.interview_content_id;
        headContent.hasTail = true;

        return newContent;
    }

    public Long getHeadId() {
        return head_id;
    }

    public void removeTail() {
        this.tail_id = null;
        this.hasTail = false;
    }
}