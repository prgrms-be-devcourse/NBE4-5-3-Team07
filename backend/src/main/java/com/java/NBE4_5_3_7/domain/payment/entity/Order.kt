package com.java.NBE4_5_3_7.domain.payment.entity

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.payment.dto.responseDto.PaymentResponseDto
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true, name = "merchant_uid")
    var merchantUid: String? = null // 주문번호

    @Column(nullable = false, name = "imp_uid")
    var impUid: String? = null // 결제 고유번호

    @Column(nullable = false, name = "item_name")
    var itemName: String? = null

    @JoinColumn(name = "member_id")
    @OneToOne(cascade = [CascadeType.ALL])
    var member: Member? = null

    @Column(nullable = false)
    var amount: BigDecimal? = null // 결제 금액

    @Column(nullable = false)
    var status: String? = null // 결제 상태 (READY, PAID, CANCELLED 등)

    var createdAt: LocalDateTime? = null // 결제 완료 시간

    constructor(paymentResponseDto: PaymentResponseDto, member: Member?) {
        this.amount = paymentResponseDto.amount
        this.status = paymentResponseDto.status
        this.createdAt = LocalDateTime.now()
        this.member = member
        this.impUid = paymentResponseDto.impUid
        this.merchantUid = paymentResponseDto.merchantUid
        this.itemName = paymentResponseDto.item_name
    }

    constructor()
}
