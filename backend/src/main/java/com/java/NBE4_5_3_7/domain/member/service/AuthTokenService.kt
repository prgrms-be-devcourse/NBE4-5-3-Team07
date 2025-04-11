package com.java.NBE4_5_3_7.domain.member.service;

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.standard.Ut
import com.java.NBE4_5_3_7.standard.Ut.Jwt.createToken
import com.java.NBE4_5_3_7.standard.Ut.Jwt.isValidToken
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
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
        val id = (payload["id"] as? Number)?.toLong() ?: return null

        val username = payload["username"] as? String ?: return null
        val nickname = payload["nickname"] as? String ?: return null

        return mapOf("id" to id, "username" to username, "nickname" to nickname)
    }

    fun getRefreshPayload(token: String): Map<String, Any>? {
        return if (!isValidToken(keyString, token)) null else Ut.Jwt.getPayload(keyString, token)
    }

    fun isTokenExpired(token: String): Boolean {
        try {
            val secretKey = Keys.hmacShaKeyFor(keyString.toByteArray())

            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parse(token)

            // 여기까지 도달했다면 토큰이 유효합니다 (만료되지 않음)
            return false
        } catch (e: Exception) {
            // 특정 예외 종류를 확인하여 만료된 토큰인지 구분할 수 있습니다
            if (e is io.jsonwebtoken.ExpiredJwtException) {
                // 만료된 토큰입니다
                return true
            }

            // 다른 이유로 유효하지 않은 토큰 (조작된 토큰 등)
            return true
        }
    }

    // RefreshToken이 유효한지 확인하는 메서드
    fun isRefreshTokenValid(refreshToken: String): Boolean {
        try {
            val payload = getRefreshPayload(refreshToken) ?: return false
            return payload["type"] == "refresh" && !isTokenExpired(refreshToken)
        } catch (e: Exception) {
            return false
        }
    }
}