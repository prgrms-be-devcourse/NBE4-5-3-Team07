package com.java.NBE4_5_3_7.domain.community.like.repository;

import com.java.NBE4_5_3_7.domain.community.like.entity.PostLike;
import com.java.NBE4_5_3_7.domain.community.post.entity.Post;
import com.java.NBE4_5_3_7.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Integer countByPostPostId(Long postId);

    Optional<PostLike> findByPostAndMember(Post post, Member member);
}
