package com.java.NBE4_5_3_7.domain.community.comment.repository

import com.java.NBE4_5_3_7.domain.community.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long?>
