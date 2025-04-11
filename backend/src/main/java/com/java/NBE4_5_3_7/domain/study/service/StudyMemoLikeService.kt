package com.java.NBE4_5_3_7.domain.study.service

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.study.entity.StudyMemoLike
import com.java.NBE4_5_3_7.domain.study.repository.StudyMemoLikeRepository
import com.java.NBE4_5_3_7.domain.study.repository.StudyMemoRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StudyMemoLikeService(
    private val studyMemoLikeRepository: StudyMemoLikeRepository,
    private val studyMemoRepository: StudyMemoRepository,
    private val redissonClient: RedissonClient
) {
    fun getLikeCount(studyMemoId: Long): Int {
        return studyMemoLikeRepository.countByStudyMemoId(studyMemoId)
    }

    fun getLikeStatus(studyMemoId: Long, member: Member): Map<String, Any> {
        val studyMemo = studyMemoRepository.findById(studyMemoId).orElseThrow()
        val liked = studyMemoLikeRepository.findByStudyMemoAndMember(studyMemo, member).isPresent
        val count = studyMemoLikeRepository.countByStudyMemoId(studyMemoId)
        return mapOf("liked" to liked, "count" to count)
    }

    fun memoLike(studyMemoId: Long, member: Member): String {
        val studyMemo = studyMemoRepository.findById(studyMemoId).orElse(null)

        val lockKey = "lock:interview:like:$studyMemoId"
        val lock = redissonClient.getLock(lockKey)

        var isLocked = false
        try {
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS)
            if (!isLocked) {
                throw RuntimeException("시스템이 바빠 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.")
            }
            val existingLike = studyMemoLikeRepository.findByStudyMemoAndMember(studyMemo, member)
            if (existingLike!!.isPresent) {
                studyMemoLikeRepository.delete(existingLike.get())
                return "좋아요 취소"
            } else {
                studyMemoLikeRepository.save(StudyMemoLike(member, studyMemo))
                return "좋아요 추가"
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("락 획득 중 인터럽트가 발생했습니다.", e)
        } finally {
            if (isLocked && lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
