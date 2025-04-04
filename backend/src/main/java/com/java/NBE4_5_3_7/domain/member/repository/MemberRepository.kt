package com.java.NBE4_5_3_7.domain.member.repository;

import com.java.NBE4_5_3_7.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Optional<Member>
    fun findByApiKey(apiKey: String): Optional<Member>
}