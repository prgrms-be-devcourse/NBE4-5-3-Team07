package com.java.NBE4_5_3_7.domain.interview.entity

import com.java.NBE4_5_3_7.domain.member.entity.Member
import jakarta.persistence.*

@Entity
open class InterviewContentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "interview_content_id", nullable = false)
    open var interviewContent: InterviewContent? = null

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    open var member: Member? = null

    constructor()

    constructor(content: InterviewContent, member: Member) : this() {
        this.interviewContent = content
        this.member = member
    }
}