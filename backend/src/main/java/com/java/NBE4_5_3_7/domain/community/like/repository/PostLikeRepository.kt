package com.java.NBE4_5_3_7.domain.community.like.repository

import com.java.NBE4_5_3_7.domain.community.like.entity.PostLike
import com.java.NBE4_5_3_7.domain.community.post.entity.Post
import com.java.NBE4_5_3_7.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostLikeRepository : JpaRepository<PostLike?, Long?> {
    fun countByPostPostId(postId: Long?): Int?

    fun findByPostAndMember(post: Post?, member: Member?): Optional<PostLike>

    // Member 엔티티에 필드명이 "id"일 경우
    fun existsByPostPostIdAndMemberId(postId: Long, memberId: Long): Boolean
}
