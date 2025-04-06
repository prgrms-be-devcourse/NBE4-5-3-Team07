package com.java.NBE4_5_3_7.domain.study.entity

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.global.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
class StudyMemoLike : BaseEntity {
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member? = null

    @JoinColumn(name = "studyMemo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var studyMemo: StudyMemo? = null

    constructor(member: Member?, studyMemo: StudyMemo?) {
        this.member = member
        this.studyMemo = studyMemo
    }

    constructor()
}
