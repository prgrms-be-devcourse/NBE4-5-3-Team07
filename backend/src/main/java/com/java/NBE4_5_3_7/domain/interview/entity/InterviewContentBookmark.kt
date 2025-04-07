package com.java.NBE4_5_3_7.domain.interview.entity

import com.java.NBE4_5_3_7.domain.member.entity.Member
import jakarta.persistence.*

@Entity
@Table(name = "interview_content_bookmark")
open class InterviewContentBookmark(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_content_id")
    open var interviewContent: InterviewContent? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    open var member: Member? = null
) {
    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var id: Long? = null
        private var interviewContent: InterviewContent? = null
        private var member: Member? = null

        fun id(id: Long?) = apply { this.id = id }
        fun interviewContent(interviewContent: InterviewContent) = apply { this.interviewContent = interviewContent }
        fun member(member: Member) = apply { this.member = member }

        fun build(): InterviewContentBookmark {
            return InterviewContentBookmark(id, interviewContent, member)
        }
    }
}