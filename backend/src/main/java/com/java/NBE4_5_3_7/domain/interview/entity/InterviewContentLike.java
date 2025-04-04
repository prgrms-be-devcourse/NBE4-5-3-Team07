package com.java.NBE4_5_3_7.domain.interview.entity;

import com.java.NBE4_5_3_7.domain.member.entity.Member;
import jakarta.persistence.*;

@Entity
public class InterviewContentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interview_content_id", nullable = false)
    private InterviewContent interviewContent;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public InterviewContentLike() {}

    public InterviewContentLike(Long id, InterviewContent interviewContent, Member member) {
        this.id = id;
        this.interviewContent = interviewContent;
        this.member = member;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private InterviewContent interviewContent;
        private Member member;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder interviewContent(InterviewContent interviewContent) {
            this.interviewContent = interviewContent;
            return this;
        }

        public Builder member(Member member) {
            this.member = member;
            return this;
        }

        public InterviewContentLike build() {
            return new InterviewContentLike(id, interviewContent, member);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterviewContent getInterviewContent() {
        return interviewContent;
    }

    public void setInterviewContent(InterviewContent interviewContent) {
        this.interviewContent = interviewContent;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}