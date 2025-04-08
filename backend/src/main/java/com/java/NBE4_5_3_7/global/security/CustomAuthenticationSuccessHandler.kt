package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.Rq
import com.java.NBE4_5_3_7.global.app.AppConfig
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
        // redirectUrl 파라미터가 있으면 사용하고, 없으면 siteFrontUrl로 fallback
        val redirectUrl = session.getAttribute("redirectUrl") as? String ?: AppConfig.SITE_FRONT_URL


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
