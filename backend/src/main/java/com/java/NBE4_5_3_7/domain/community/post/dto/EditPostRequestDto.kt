package com.java.NBE4_5_3_7.domain.community.post.dto

class EditPostRequestDto {
    var postId: Long? = null
    var title: String? = null
    var content: String? = null

    constructor()

    constructor(postId: Long?, title: String?, content: String?) {
        this.postId = postId
        this.title = title
        this.content = content
    }
}
