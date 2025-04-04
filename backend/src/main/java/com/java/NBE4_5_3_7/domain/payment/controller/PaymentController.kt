package com.java.NBE4_5_3_7.domain.payment.controller

import com.java.NBE4_5_3_7.domain.payment.dto.reqestDto.PaymentRequestDto
import com.java.NBE4_5_3_7.domain.payment.dto.responseDto.PaymentResponseDto
import com.java.NBE4_5_3_7.domain.payment.service.PaymentService

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController {
    private val paymentService: PaymentService? = null

    @PostMapping("/verify")
    fun verifyPayment(@RequestBody requestDto: PaymentRequestDto): ResponseEntity<PaymentResponseDto> {
        return ResponseEntity.ok(paymentService!!.verifyPayment(requestDto))
    }

    @PostMapping("/webhook")
    @Throws(InterruptedException::class)
    fun handleWebhook(@RequestBody payload: Map<String?, Any?>): ResponseEntity<String> {
        paymentService!!.handleWebhook(payload)
        return ResponseEntity.ok("웹훅 조회 성공")
    }

    @PostMapping("/cancel")
    fun cancelledPayments(): ResponseEntity<Map<String, String>> {
        paymentService!!.cancelSubscription()
        val response: MutableMap<String, String> = HashMap()
        response["message"] = "구독 취소 성공"
        return ResponseEntity.ok(response)
    }
}