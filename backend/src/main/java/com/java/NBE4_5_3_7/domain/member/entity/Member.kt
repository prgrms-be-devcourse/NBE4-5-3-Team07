package com.java.NBE4_5_3_7.domain.member.entity

import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContentBookmark
import com.java.NBE4_5_3_7.global.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

@Entity
class Member() : BaseEntity() {
    @Column(length = 100, unique = true)
    lateinit var username: String

    @Column(length = 100, unique = true)
    lateinit var apiKey: String

    @Column(length = 100)
    lateinit var nickname: String

    lateinit var profileImgUrl: String

    @Enumerated(EnumType.STRING)
    lateinit var role: Role

    @Enumerated(EnumType.STRING)
    lateinit var subscriptionPlan: SubscriptionPlan

    lateinit var subscribeEndDate: LocalDateTime

    constructor(id: Long, username: String, nickname: String): this() {
        this.id = id
        this.username = username
        this.nickname = nickname
        this.profileImgUrl = ""
    }

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    var bookmarks: MutableList<InterviewContentBookmark> = mutableListOf()

    val isAdmin: Boolean
        get()  = Role.ADMIN == role

    fun getAuthorities(): Collection<GrantedAuthority> {
        return getMemberAuthoritesAsString()
            .map { SimpleGrantedAuthority(it) }
    }

    fun getMemberAuthoritesAsString(): List<String> {
        val authorities = ArrayList<String>()
        authorities.add("ROLE_USER")

        if (isAdmin) {
            authorities.add("ROLE_ADMIN")
        } else {
            authorities.add("ROLE_USER")
        }

        return authorities
    }

    fun update(nickname: String) {
        this.nickname = nickname
    }

    fun getProfileImgUrlOrDefaultUrl(): String =
        profileImgUrl.takeUnless { it.isBlank() } ?: "https://placehold.co/640x640?text=O_O"
}