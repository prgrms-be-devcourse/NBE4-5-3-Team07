package com.java.NBE4_5_3_7.global

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.exception.ServiceException
import com.java.NBE4_5_3_7.global.security.SecurityUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class Rq(
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
    private val memberService: MemberService
) {

    fun setLogin(actor: Member) {
        val freshActor = memberService.findById(actor.id)
            .orElseThrow {
                ServiceException("401-3", "사용자 정보를 찾을 수 없습니다")
            }

        val user: UserDetails = SecurityUser(
            freshActor.id, freshActor.username,
            freshActor.nickname, freshActor.getAuthorities()
        )

        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(user, null, user.authorities)
    }

    val actor: Member
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication == null || !authentication.isAuthenticated) {
                throw ServiceException("401-2", "로그인이 필요합니다.")
            }

            val principal = authentication.principal
            if (principal is SecurityUser) {
                return memberService.findById(principal.id)
                    .orElseThrow {
                        ServiceException("401-3", "인증 정보가 올바르지 않습니다")
                    }
            }
            throw ServiceException("401-3", "인증 정보가 올바르지 않습니다")
        }

    val actorOrNull: Member?
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication == null || !authentication.isAuthenticated) {
                return null
            }

            val principal = authentication.principal
            return if (principal is SecurityUser) {
                memberService.findById(principal.id).orElse(null)
            } else null
        }

    fun getValueFromCookie(name: String): String? {
        val cookies = request.cookies ?: return null
        for (cookie in cookies) {
            if (cookie.name == name) return cookie.value
        }
        return null
    }

    fun addCookie(name: String, value: String, maxAge: Int = 60 * 60 * 24) {
        val cookie = Cookie(name, value)
        cookie.path = "/"
        cookie.isHttpOnly = true
        cookie.secure = true
        cookie.maxAge = maxAge

        // SameSite 속성 설정
        cookie.setAttribute("SameSite", "Strict")

        val serverName = request.serverName
        val isProd = !serverName.equals("localhost", ignoreCase = true)
        if (isProd) {
            cookie.domain = "www.devprep.shop"
        }

        response.addCookie(cookie)
    }

//    fun deleteCookie(name: String) {
//        val cookie = Cookie(name, "")
//        cookie.path = "/"
//        cookie.maxAge = 0
//        cookie.isHttpOnly = true
//        cookie.secure = true
//        cookie.setAttribute("SameSite", "None")
//
//        val serverName = request.serverName
//        val isProd = !serverName.equals("localhost", ignoreCase = true)
//        if (isProd) {
//            cookie.domain = "www.devprep.shop"
//        }
//
//        response.addCookie(cookie)
//    }

    fun getRealActor(actor: Member): Member {
        return memberService.findById(actor.id).orElseThrow {
            RuntimeException("해당 멤버를 찾을 수 없습니다.")
        }
    }
}
