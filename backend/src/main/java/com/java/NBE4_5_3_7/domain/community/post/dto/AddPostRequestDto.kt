package com.java.NBE4_5_3_7.domain.community.post.dto

class AddPostRequestDto {
    var title: String? = null
    var content: String? = null

    constructor()

    constructor(title: String?, content: String?) {
        this.title = title
        this.content = content
    }
}