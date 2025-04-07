package com.java.NBE4_5_3_7.domain.interviewComment.entity

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.member.entity.Member
import jakarta.persistence.*

@Entity
open class InterviewContentComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val commentId: Long? = null,

    @Lob
    open var answer: String? = null,

    @Column(name = "is_public")
    open var isPublic: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_content_id", nullable = false)
    open var interviewContent: InterviewContent? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    open var member: Member? = null
)