package com.java.NBE4_5_3_7.domain.study.entity

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.global.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class StudyMemo : BaseEntity {
    @JvmField
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyContent_id")
    var studyContent: StudyContent? = null

    @JvmField
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null

    @JvmField
    @Column(columnDefinition = "TEXT")
    var memoContent: String? = null

    @JvmField
    var isPublished: Boolean = false

    @CreatedDate
    @Column(updatable = false) // 생성 날짜는 수정되지 않도록 설정
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

    @OneToMany(mappedBy = "studyMemo", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var likes: MutableList<StudyMemoLike> = mutableListOf()

    constructor(memoContent: String?, studyContent: StudyContent?, member: Member?, isPublished: Boolean) {
        this.memoContent = memoContent
        this.studyContent = studyContent
        this.member = member
        this.isPublished = isPublished
    }

    constructor()
}
