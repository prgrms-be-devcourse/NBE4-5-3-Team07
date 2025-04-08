package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.Rq
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val rq: Rq,
    private val memberService: MemberService
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val session = request.session
        // 엘비스 연산자로 localhost 기본값 지정
        var redirectUrl = session.getAttribute("redirectUrl") as? String ?: "http://localhost:3000"
        session.removeAttribute("redirectUrl")

        val member = rq.actor
        val accessToken = memberService.genAccessToken(member)
        val refreshToken = memberService.genRefreshToken(member)

        rq.addCookie("accessToken", accessToken)
        rq.addCookie("refreshToken", refreshToken)
        rq.addCookie("apiKey", member.apiKey)

        response.sendRedirect(redirectUrl)
    }
}
