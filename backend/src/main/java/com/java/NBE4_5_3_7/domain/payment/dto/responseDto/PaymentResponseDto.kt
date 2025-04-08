package com.java.NBE4_5_3_7.domain.payment.dto.responseDto

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.siot.IamportRestClient.response.Payment
import java.math.BigDecimal

class PaymentResponseDto(payment: Payment, member: Member) {
    var impUid: String = payment.impUid // 아임포트 결제 고유번호
    var merchantUid: String = payment.merchantUid // 주문번호
    var buyerName: String = member.nickname // 구매자 이름
    var buyerEmail: String = payment.buyerEmail // 구매자 이메일
    var amount: BigDecimal = payment.amount // 결제 금액
    var status: String = payment.status // 결제 상태 (paid, cancelled 등)
    var pay_method: String = payment.payMethod
    var success = true
    var card_name: String = payment.cardName
    var item_name: String = payment.name
}

