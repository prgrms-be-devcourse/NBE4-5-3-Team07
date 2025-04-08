package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.domain.member.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.core.user.OAuth2User

class SecurityUser(
    val id: Long,
    username: String,
    val nickname: String,
    authorities: Collection<out GrantedAuthority>
) : User(username, "", authorities), OAuth2User {

    constructor(member: Member) : this(
        member.id,
        member.username,
        member.nickname,
        member.getAuthorities()
    )

    override fun <A : Any?> getAttribute(name: String?): A? {
        return null
    }

    override fun getAttributes(): Map<String, Any> = emptyMap()

    override fun getName(): String = username
}
