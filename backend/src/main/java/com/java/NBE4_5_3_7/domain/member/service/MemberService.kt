package com.java.NBE4_5_3_7.domain.member.service

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.Role
import com.java.NBE4_5_3_7.domain.member.entity.SubscriptionPlan
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import com.java.NBE4_5_3_7.global.Rq
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val authTokenService: AuthTokenService,
    private val rq: Rq
) {

    @Transactional
    fun join(username: String, nickname: String, profileImgUrl: String): Member {
        val member = Member().apply {
            this.username = username
            this.apiKey = username
            this.nickname = nickname
            this.profileImgUrl = profileImgUrl
            this.role = Role.USER
            this.subscriptionPlan = SubscriptionPlan.FREE
            this.subscribeEndDate = LocalDateTime.now()
        }
        return memberRepository.save(member)
    }

    fun findByUsername(username: String): Optional<Member> {
        return memberRepository.findByUsername(username)
    }

    fun findById(id: Long): Optional<Member> {
        return memberRepository.findById(id)
    }

    fun findByApiKey(apiKey: String): Optional<Member> {
        return memberRepository.findByApiKey(apiKey)
    }

    fun getAuthToken(member: Member): String {
        return member.apiKey + " " + authTokenService.genAccessToken(member)
    }

    fun getMemberByAccessToken(accessToken: String?): Optional<Member> {
        if (accessToken == null) return Optional.empty()

        val payload = authTokenService.getPayload(accessToken)
            ?: return Optional.empty()

//        val id = payload["id"] as Long
        val id = when (val rawId = payload["id"]) {
            is Int -> rawId.toLong()
            is Long -> rawId
            else -> throw IllegalArgumentException("Invalid id type in token payload: ${rawId?.javaClass}")
        }
        val username = payload["username"] as String?
        val nickname = payload["nickname"] as String?

//        return Optional.of(
//            Member(
//                id,
//                username!!,
//                nickname!!
//            )
//        )
        return memberRepository.findById(id)
    }

    fun getRefreshPayload(refreshToken: String): Map<String, Any>? {
        return try {
            authTokenService.getRefreshPayload(refreshToken)
                ?.takeIf { it["type"] == "refresh" }
        } catch (e: Exception) {
            null
        }
    }

    fun getIdFromRq(): Long = rq.actor.id!!

    fun genRefreshToken(member: Member): String = authTokenService.genRefreshToken(member)

    fun genAccessToken(member: Member): String = authTokenService.genAccessToken(member)

    fun getMemberFromRq(): Member = rq.actor

    @Transactional
    fun changeRole(id: Long): String {
        val member = memberRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 멤버를 찾을 수 없습니다.") }

        require(member.isAdmin) { "권한 변경 요청은 관리자만 가능합니다." }

        member.role = if (member.role == Role.ADMIN) Role.USER
            else Role.ADMIN

        return "Member [$id] 의 권한이 ${member.role} 로 변경되었습니다."
    }

    fun isAdmin(id: Long?): Boolean {
        val member = memberRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 멤버를 찾을 수 없습니다.") }
        return member.isAdmin
    }

    @Transactional
    fun saveMember(member: Member) {
        memberRepository.save(member)
    }
}
