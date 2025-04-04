package com.java.NBE4_5_3_7.domain.community.comment.dto

class AddCommentRequestDto {
    // Getter & Setter
    var articleId: Long? = null
    var comment: String? = null
    var parentId: Long? = null

    // 기본 생성자 (NoArgsConstructor)
    constructor()

    // 모든 필드 생성자 (AllArgsConstructor)
    constructor(articleId: Long?, comment: String?, parentId: Long?) {
        this.articleId = articleId
        this.comment = comment
        this.parentId = parentId
    }
}
