package com.java.NBE4_5_3_7.domain.payment.entity

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.payment.dto.responseDto.PaymentResponseDto
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // 주문 ID (기본값 null, auto-increment)

    @Column(nullable = false, unique = true, name = "merchant_uid")
    var merchantUid: String, // 주문번호

    @Column(nullable = false, name = "imp_uid")
    var impUid: String, // 결제 고유번호

    @Column(nullable = false, name = "item_name")
    val itemName: String, // 상품명

    @JoinColumn(name = "member_id")
    @OneToOne(cascade = [CascadeType.ALL])
    val member: Member, // 회원

    @Column(nullable = false)
    val amount: BigDecimal, // 결제 금액

    @Column(nullable = false)
    val status: String, // 결제 상태 (READY, PAID, CANCELLED 등)

    var createdAt: LocalDateTime = LocalDateTime.now() // 결제 완료 시간
) {
    constructor(paymentResponseDto: PaymentResponseDto, member: Member) : this(
        merchantUid = paymentResponseDto.merchantUid,
        impUid = paymentResponseDto.impUid,
        itemName = paymentResponseDto.item_name,
        amount = paymentResponseDto.amount,
        status = paymentResponseDto.status,
        member = member
    )
}
