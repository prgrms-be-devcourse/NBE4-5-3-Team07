package com.java.NBE4_5_3_7.domain.study.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.java.NBE4_5_3_7.domain.member.entity.Member;
import com.java.NBE4_5_3_7.global.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StudyMemoLike extends BaseEntity {

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "studyMemo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private StudyMemo studyMemo;

    public StudyMemoLike(Member member, StudyMemo studyMemo) {
        this.member = member;
        this.studyMemo = studyMemo;
    }
}
