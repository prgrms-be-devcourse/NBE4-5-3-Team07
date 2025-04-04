package com.java.NBE4_5_3_7.domain.community.comment.dto

class EditCommentRequestDto {
    // Getter & Setter
    var commentId: Long? = null
    var comment: String? = null

    // 기본 생성자
    constructor()

    // 전체 필드 생성자
    constructor(commentId: Long?, comment: String?) {
        this.commentId = commentId
        this.comment = comment
    }
}
