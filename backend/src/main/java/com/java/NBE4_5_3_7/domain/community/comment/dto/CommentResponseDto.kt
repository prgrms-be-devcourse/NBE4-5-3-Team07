package com.java.NBE4_5_3_7.domain.community.comment.dto

import java.time.LocalDateTime

class CommentResponseDto {
    // Getter & Setter
    var articleId: Long? = null
    var commentId: Long? = null
    var commentAuthorName: String? = null
    var commentTime: LocalDateTime? = null
    var comment: String? = null
    var reCommentCount: Int? = null
    var isMyComment: Boolean = false

    // 기본 생성자 (NoArgsConstructor)
    constructor()

    // 전체 필드 생성자 (AllArgsConstructor)
    constructor(
        articleId: Long?, commentId: Long?, commentAuthorName: String?,
        commentTime: LocalDateTime?, comment: String?, reCommentCount: Int?,
        myComment: Boolean
    ) {
        this.articleId = articleId
        this.commentId = commentId
        this.commentAuthorName = commentAuthorName
        this.commentTime = commentTime
        this.comment = comment
        this.reCommentCount = reCommentCount
        this.isMyComment = myComment
    }
}
