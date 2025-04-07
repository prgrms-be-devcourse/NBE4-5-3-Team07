package com.java.NBE4_5_3_7.domain.payment.repository

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.payment.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByImpUid(impUid: String?): Optional<Order>

    fun findByMemberAndStatus(member: Member?, cancelled: String?): Optional<Order>
}
