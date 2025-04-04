package com.java.NBE4_5_3_7.domain.community.post.dto

import java.time.LocalDateTime

class PostListResponseDto {
    var postId: Long? = null
    var title: String? = null
    var author: String? = null
    var createdAt: LocalDateTime? = null

    constructor()

    constructor(postId: Long?, title: String?, author: String?, createdAt: LocalDateTime?) {
        this.postId = postId
        this.title = title
        this.author = author
        this.createdAt = createdAt
    }
}