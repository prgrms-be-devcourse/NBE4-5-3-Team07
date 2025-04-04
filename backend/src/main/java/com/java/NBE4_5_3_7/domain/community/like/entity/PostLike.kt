package com.java.NBE4_5_3_7.domain.community.like.entity

import com.java.NBE4_5_3_7.domain.community.post.entity.Post
import com.java.NBE4_5_3_7.domain.member.entity.Member
import jakarta.persistence.*

@Entity
class PostLike {
    // Getter & Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    var post: Post? = null

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member? = null

    // 기본 생성자
    constructor()

    // 전체 필드 생성자
    constructor(id: Long?, post: Post?, member: Member?) {
        this.id = id
        this.post = post
        this.member = member
    }
}
