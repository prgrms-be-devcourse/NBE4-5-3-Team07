package com.java.NBE4_5_3_7.domain.community.post.service

import com.java.NBE4_5_3_7.domain.community.like.dto.LikeResponseDto
import com.java.NBE4_5_3_7.domain.community.like.entity.PostLike
import com.java.NBE4_5_3_7.domain.community.like.repository.PostLikeRepository
import com.java.NBE4_5_3_7.domain.community.post.repository.PostRepository
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import org.redisson.api.RedissonClient
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PostLikeTransaction(
    private val likeRepository: PostLikeRepository,
    private val postRepository: PostRepository,
    private val memberRepository: MemberRepository,
    private val redissonClient: RedissonClient
) {

    @Transactional
    fun toggle(memberId: Long, postId: Long): LikeResponseDto {
        val post = postRepository.findById(postId)
            .orElseThrow { RuntimeException("해당 게시글을 찾을 수 없습니다.") }

        val member = memberRepository.findById(memberId)
            .orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }

        val redisKey = "post:like:$postId"
        val atomicLong = redissonClient.getAtomicLong(redisKey)

        val existingLike = likeRepository.findByPostAndMember(post, member)

        val newCount = if (existingLike.isPresent) {
            likeRepository.delete(existingLike.get())
            atomicLong.decrementAndGet()
        } else {
            try {
                likeRepository.save(PostLike(null, post, member))
                atomicLong.incrementAndGet()
            } catch (e: DataIntegrityViolationException) {
                atomicLong.get()
            }
        }

        val message = if (existingLike.isPresent) "좋아요 취소" else "좋아요 추가"
        return LikeResponseDto(postId, newCount.toInt(), message)
    }
}
