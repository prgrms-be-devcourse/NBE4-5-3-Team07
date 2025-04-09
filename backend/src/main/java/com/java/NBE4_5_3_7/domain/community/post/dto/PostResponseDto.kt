package com.java.NBE4_5_3_7.domain.community.post.dto

import com.java.NBE4_5_3_7.domain.community.comment.dto.CommentResponseDto
import java.time.LocalDateTime

class PostResponseDto {
    var id: Long? = null
    var authorName: String? = null
    var postTime: LocalDateTime? = null
    var title: String? = null
    var content: String? = null
    var like: Int? = null
    var likedByCurrentUser: Boolean? = null
    var comments: List<CommentResponseDto>? = null


    constructor()

    constructor(
        id: Long?, authorName: String?, postTime: LocalDateTime?,
        title: String?, content: String?, like: Int?,
        likedByCurrentUser: Boolean?,
        comments: List<CommentResponseDto>?
    ) {
        this.id = id
        this.authorName = authorName
        this.postTime = postTime
        this.title = title
        this.content = content
        this.like = like
        this.likedByCurrentUser = likedByCurrentUser
        this.comments = comments
    }
}

