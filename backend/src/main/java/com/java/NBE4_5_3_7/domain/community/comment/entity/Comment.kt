package com.java.NBE4_5_3_7.domain.community.comment.entity

import com.java.NBE4_5_3_7.domain.community.post.entity.Post
import com.java.NBE4_5_3_7.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Comment {
    // Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Column(columnDefinition = "TEXT", nullable = false)
    var comment: String? = null
        private set

    @Column(name = "created_date")
    @CreatedDate
    var createdDate: LocalDateTime? = null
        private set

    @Column(name = "modified_date")
    @LastModifiedDate
    var modifiedDate: LocalDateTime? = null
        private set

    @ManyToOne
    @JoinColumn(name = "post_id")
    var post: Post? = null
        private set

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null
        private set

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableList<Comment> = mutableListOf()
        private set


    @ManyToOne
    @JoinColumn(name = "member_id")
    var author: Member? = null
        private set

    // 기본 생성자 (JPA 필수)
    constructor()

    constructor(
        comment: String,
        post: Post,
        parent: Comment? = null,
        author: Member
    ) {
        this.comment = comment
        this.post = post
        this.parent = parent
        this.author = author
    }

    // 수정 메서드
    fun update(newComment: String?) {
        this.comment = newComment
    }
}
