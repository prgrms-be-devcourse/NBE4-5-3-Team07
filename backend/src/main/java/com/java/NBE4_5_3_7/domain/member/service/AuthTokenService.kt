package com.java.NBE4_5_3_7.domain.member.service;

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.standard.Ut
import com.java.NBE4_5_3_7.standard.Ut.Jwt.createToken
import com.java.NBE4_5_3_7.standard.Ut.Jwt.isValidToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthTokenService(
    @Value("\${custom.jwt.secret-key}")
    val keyString: String,
    @Value("\${custom.jwt.expire-seconds}")
    val expireSeconds: Int = 0, // Access Token 만료시간 (예: 60초)
    @Value("\${custom.jwt.refresh-expire-seconds}")
    val refreshExpireSeconds: Int = 0// Refresh Token 만료시간 (예: 3600초 = 1시간)
) {

    fun genAccessToken(member: Member): String {
        return createToken(
            keyString,
            expireSeconds,
            mapOf("id" to member.id, "username" to member.username, "nickname" to member.nickname)
        )
    }

    fun genRefreshToken(member: Member): String {
        return createToken(
            keyString,
            refreshExpireSeconds,
            mapOf("id" to member.id, "type" to "refresh")
        )
    }

    fun getPayload(token: String): Map<String, Any>? {
        if (!isValidToken(keyString, token)) return null

        val payload = Ut.Jwt.getPayload(keyString, token)
        val idNo = payload["id"] as Number
        val id = idNo.toLong()

        val username = payload["username"] as? String ?: return null
        val nickname = payload["nickname"] as? String ?: return null

        return mapOf("id" to id, "username" to username, "nickname" to nickname)
    }

    fun getRefreshPayload(token: String): Map<String, Any>? {
        return if (!isValidToken(keyString, token)) null else Ut.Jwt.getPayload(keyString, token)
    }
}