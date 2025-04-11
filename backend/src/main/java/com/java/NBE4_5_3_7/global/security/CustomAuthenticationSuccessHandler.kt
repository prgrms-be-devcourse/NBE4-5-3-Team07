package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.Rq
import com.java.NBE4_5_3_7.global.app.AppConfig
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val rq: Rq,
    private val memberService: MemberService,
    @Value("\${custom.jwt.expire-seconds}")
    private val expireSeconds: Int = 1800,
    @Value("\${custom.jwt.refresh-expire-seconds}")
    private val refreshExpireSeconds: Int = 604800
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val session = request.session
        // redirectUrl 파라미터가 있으면 사용하고, 없으면 siteFrontUrl로 fallback
        val redirectUrl = session.getAttribute("redirectUrl") as? String ?: AppConfig.SITE_FRONT_URL


        session.removeAttribute("redirectUrl")

        val member = rq.actor
        val accessToken = memberService.genAccessToken(member)
        val refreshToken = memberService.genRefreshToken(member)

        rq.addCookie("accessToken", accessToken, expireSeconds)
        rq.addCookie("refreshToken", refreshToken, refreshExpireSeconds)
        rq.addCookie("apiKey", member.apiKey, refreshExpireSeconds) // apiKey는 refreshToken과 동일한 만료 시간 사용
        response.sendRedirect(redirectUrl)
    }
}
