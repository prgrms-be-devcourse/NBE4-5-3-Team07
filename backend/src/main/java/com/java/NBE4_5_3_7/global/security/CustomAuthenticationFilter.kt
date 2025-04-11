package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.domain.member.service.AuthTokenService
import com.java.NBE4_5_3_7.global.Rq
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class CustomAuthenticationFilter(
    private val rq: Rq,
    private val memberService: MemberService,
    private val authTokenService: AuthTokenService,
    @Value("\${custom.jwt.expire-seconds}")
    private val expireSeconds: Int = 1800
) : OncePerRequestFilter() {

    private data class TokenPair(val accessToken: String, val refreshToken: String, val apiKey: String)


    private fun getTokensFromCookie(): TokenPair? {
        val accessToken = rq.getValueFromCookie("accessToken")
        val refreshToken = rq.getValueFromCookie("refreshToken")
        val apiKey = rq.getValueFromCookie("apiKey")

        return if (accessToken == null || refreshToken == null || apiKey == null) {
            // 이 부분을 수정: refreshToken과 apiKey만 있어도 토큰 갱신 시도
            if (refreshToken != null && apiKey != null) {
                TokenPair("", refreshToken, apiKey) // 빈 accessToken과 함께 진행
            } else {
                null
            }
        } else {
            TokenPair(accessToken, refreshToken, apiKey)
        }
    }

    private fun getMemberByTokens(accessToken: String, refreshToken: String, apiKey: String): Member? {
        // accessToken이 비어있는 경우, 즉시 refreshToken으로 갱신 시도
        if (accessToken.isEmpty()) {
            return refreshAccessToken(refreshToken, apiKey)
        }

        // 기존 accessToken 검증 로직
        val isExpired = authTokenService.isTokenExpired(accessToken)

        if (!isExpired) {
            val accMember = memberService.getMemberByAccessToken(accessToken)
            if (accMember.isPresent) {
                return accMember.get()
            }
        }

        // accessToken이 만료되었거나 유효하지 않은 경우 refreshToken 사용
        return refreshAccessToken(refreshToken, apiKey)
    }

    private fun refreshAccessToken(refreshToken: String, apiKey: String): Member? {
        // refreshToken 검증
        if (!authTokenService.isRefreshTokenValid(refreshToken)) {
            return null
        }

        // apiKey로 회원 조회
        val member = memberService.findByApiKey(apiKey).orElse(null)
        if (member == null) {
            return null
        }

        // 새로운 accessToken 발급
        val newAccessToken = memberService.genAccessToken(member)
        rq.addCookie("accessToken", newAccessToken, expireSeconds)

        return member
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val url = request.requestURI

        if (
            url.startsWith("/ws/chat") ||
            url.startsWith("/chat/messages") ||
            url == "/member/logout"
        ) {
            filterChain.doFilter(request, response)
            return
        }


        val tokens = getTokensFromCookie()
        if (tokens == null) {
            filterChain.doFilter(request, response)
            return
        }

        val actor = getMemberByTokens(tokens.accessToken, tokens.refreshToken, tokens.apiKey)
        if (actor == null) {
            // 인증 실패 시 쿠키 삭제
            rq.deleteCookie("accessToken")
            rq.deleteCookie("refreshToken")
            rq.deleteCookie("apiKey")

            filterChain.doFilter(request, response)
            return
        }

        val freshActor = memberService.findById(actor.id).orElse(null)
        rq.setLogin(freshActor ?: actor)

        filterChain.doFilter(request, response)
    }
}