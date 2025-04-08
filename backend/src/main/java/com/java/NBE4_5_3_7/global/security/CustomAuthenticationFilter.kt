package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.Rq
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class CustomAuthenticationFilter(
    private val rq: Rq,
    private val memberService: MemberService
) : OncePerRequestFilter() {

    private data class TokenPair(val accessToken: String, val refreshToken: String, val apiKey: String)

    private fun getTokensFromCookie(): TokenPair? {
        val accessToken = rq.getValueFromCookie("accessToken")
        val refreshToken = rq.getValueFromCookie("refreshToken")
        val apiKey = rq.getValueFromCookie("apiKey")
        return if (accessToken == null || refreshToken == null || apiKey == null) {
            null
        } else {
            TokenPair(accessToken, refreshToken, apiKey)
        }
    }

    private fun getMemberByTokens(accessToken: String, refreshToken: String, apiKey: String): Member? {
        // accessToken으로 조회
        val accMember = memberService.getMemberByAccessToken(accessToken)
        if (accMember.isPresent) {
            return accMember.get()
        }

        //Access Token 실패 → Refresh Token 검증
        val refreshPayload = memberService.getRefreshPayload(refreshToken)
        if (refreshPayload == null || refreshPayload["type"] != "refresh") {
            return null
        }
        // apiKey로 회원 조회 후 accessToken 재발급
        val member = memberService.findByApiKey(apiKey).orElse(null) ?: return null
        val newAccessToken = memberService.genAccessToken(member)
        rq.addCookie("accessToken", newAccessToken)

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

        val tokens = getTokensFromCookie() ?: run {
            filterChain.doFilter(request, response)
            return
        }

        val actor = getMemberByTokens(tokens.accessToken, tokens.refreshToken, tokens.apiKey) ?: run {
            filterChain.doFilter(request, response)
            return
        }

        val freshActor = memberService.findById(actor.id).orElse(null)
        rq.setLogin(freshActor ?: actor)

        filterChain.doFilter(request, response)
    }
}
