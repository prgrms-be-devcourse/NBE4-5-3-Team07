package com.java.NBE4_5_3_7.domain.community.post.entity

import com.java.NBE4_5_3_7.domain.community.comment.entity.Comment
import com.java.NBE4_5_3_7.domain.community.like.entity.PostLike
import com.java.NBE4_5_3_7.domain.community.post.dto.EditPostRequestDto
import com.java.NBE4_5_3_7.domain.member.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Post {
    // Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var postId: Long? = null
        private set

    @Column(name = "title", nullable = false)
    var title: String? = null
        private set

    @Column(name = "content", columnDefinition = "TEXT")
    var content: String? = null
        private set

    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    var author: Member? = null
        private set

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null
        private set

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
        private set

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @OrderBy("id asc")
    var comments: List<Comment>? = null
        private set

    @OneToMany(mappedBy = "post", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var likes: List<PostLike>? = null
        private set

    // 기본 생성자 (NoArgsConstructor)
    constructor()

    // 전체 필드 생성자 (AllArgsConstructor)
    constructor(
        postId: Long?, title: String?, content: String?, author: Member?,
        createdAt: LocalDateTime?, updatedAt: LocalDateTime?,
        comments: List<Comment>?, likes: List<PostLike>?
    ) {
        this.postId = postId
        this.title = title
        this.content = content
        this.author = author
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.comments = comments
        this.likes = likes
    }

    // 수정 메서드
    fun update(editPostRequestDto: EditPostRequestDto) {
        this.title = editPostRequestDto.title
        this.content = editPostRequestDto.content
    }
}
