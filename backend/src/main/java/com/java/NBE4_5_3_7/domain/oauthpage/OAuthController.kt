package com.java.NBE4_5_3_7.domain.oauthpage

import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class OAuthController {

    @GetMapping(value = ["/"], produces = ["text/plain;charset=UTF-8"])
    @ResponseBody
    fun home(): String {
        return "API 서버에 오신 걸 환영합니다."
    }

    @GetMapping("/info")
    @ResponseBody
    fun session(session: HttpSession): Map<String, Any?> {
        val sessionMap = mutableMapOf<String, Any?>()
        val names = session.attributeNames

        while (names.hasMoreElements()) {
            val name = names.nextElement()
            sessionMap[name] = session.getAttribute(name)
        }

        return sessionMap
    }
}
