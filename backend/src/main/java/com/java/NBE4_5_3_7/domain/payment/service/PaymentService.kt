package com.java.NBE4_5_3_7.domain.payment.service

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.SubscriptionPlan
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.domain.payment.dto.reqestDto.PaymentRequestDto
import com.java.NBE4_5_3_7.domain.payment.dto.responseDto.PaymentResponseDto
import com.java.NBE4_5_3_7.domain.payment.entity.Order
import com.java.NBE4_5_3_7.domain.payment.repository.OrderRepository
import com.siot.IamportRestClient.IamportClient
import com.siot.IamportRestClient.exception.IamportResponseException
import com.siot.IamportRestClient.response.IamportResponse
import com.siot.IamportRestClient.response.Payment
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDateTime

@Service
@Transactional
@RequiredArgsConstructor
class PaymentService(
    private val orderRepository: OrderRepository,
    private val memberService: MemberService,
    private var iamportClient: IamportClient
) {
    @Value("\${imp.key}")
    private val apiKey: String? = null

    @Value("\${imp.secret_key}")
    private val apiSecret: String? = null

    @PostConstruct
    fun initialize() {
        iamportClient = IamportClient(apiKey, apiSecret)
    }

    @Transactional
    @CircuitBreaker(name = "iamport", fallbackMethod = "fallbackVerifyPayment")
    fun verifyPayment(requestDto: PaymentRequestDto): PaymentResponseDto {
        val member = memberService.getMemberFromRq()
        try {
            val impUid: String? = requestDto.imp_uid
            val paymentResponse = iamportClient.paymentByImpUid(impUid)

            if (paymentResponse == null || paymentResponse.response == null) {
                throw RuntimeException("결제 정보를 가져올 수 없습니다.")
            }

            val payment = paymentResponse.response
            val responseDto = PaymentResponseDto(payment, member)
            saveOrder(responseDto, member)

            return responseDto
        } catch (e: Exception) {
            throw RuntimeException("결제 검증 중 오류 발생: " + e.message)
        }
    }

    fun fallbackVerifyPayment(requestDto: PaymentRequestDto, throwable: Throwable): PaymentResponseDto {
        throw RuntimeException("결제 서비스가 현재 이용 불가능합니다. 잠시 후 다시 시도해주세요.")
    }

    @Transactional
    fun saveOrder(paymentResponseDto: PaymentResponseDto, member: Member?) {
        val order = orderRepository.findByMemberAndStatus(member, "cancelled")
        if (order.isPresent) {
            order.get().createdAt = LocalDateTime.now() // 결제 검증 시간
            order.get().merchantUid = paymentResponseDto.merchantUid
            order.get().impUid = paymentResponseDto.impUid
            orderRepository.save(order.get())
        } else {
            orderRepository.save(Order(paymentResponseDto, member))
        }
    }

    // 웹훅에서 결제 상태 처리
    @Throws(InterruptedException::class)
    fun handleWebhook(payload: Map<String?, Any?>) {
        Thread.sleep(3000)
        val impUid = payload["imp_uid"] as String? // 웹훅에서 imp_uid 가져오기
        requireNotNull(impUid) { "존재하지 않는 imp 번호 입니다." }

        // 포트원 API를 호출해 결제 정보 조회
        val paymentResponse = getPaymentData(impUid)

        if (paymentResponse == null || paymentResponse.response == null) {
            throw RuntimeException("결제 정보 조회 실패")
        }
        updatePaymentStatus(paymentResponse.response)
    }

    // 결제 상태 업데이트
    fun updatePaymentStatus(payment: Payment) {
        val orderOptional = orderRepository.findByImpUid(payment.impUid)

        if (orderOptional.isPresent) {
            val orderEntity = orderOptional.get()
            val member = orderEntity.member

            // 주문 상태 업데이트
            orderEntity.status = payment.status
            orderEntity.amount = payment.amount

            // 결제 상품이 "PREMIUM"이면 Member의 구독 플랜 변경
            if ("PREMIUM" == payment.name) {
                println("PREMIUM 상품 결제 확인됨. 회원 ${member?.username} 의 구독 상태를 PREMIUM으로 변경합니다.")
                if (member != null) {
                    member.subscriptionPlan = SubscriptionPlan.PREMIUM
                    member.subscribeEndDate = LocalDateTime.now().plusDays(30)
                }
            }

            orderRepository.save(orderEntity)
        } else {
            println("결제 정보가 존재하지 않음: " + payment.impUid)
        }
    }

    // 아임포트 결제 정보 조회
    fun getPaymentData(impUid: String?): IamportResponse<Payment>? {
        try {
            return iamportClient.paymentByImpUid(impUid)
        } catch (e: IamportResponseException) {
            println("아임포트 API 호출 실패: " + e.message)
            return null
        } catch (e: IOException) {
            println("아임포트 API 호출 실패: " + e.message)
            return null
        }
    }

    // 구독 취소 기능
    fun cancelSubscription() {
        // 1. 회원 정보 조회
        val member = memberService.getMemberFromRq()

        requireNotNull(member) { "존재하지 않는 회원입니다." }

        // 2. 회원의 현재 구독 플랜 상태 확인
        require(SubscriptionPlan.FREE != member.subscriptionPlan) { "무료 플랜은 취소할 수 없습니다." }

        // 3. 구독 플랜을 취소 처리
        member.subscriptionPlan = SubscriptionPlan.FREE // FREE로 설정 (구독 취소)
        memberService.saveMember(member) // 회원 정보 업데이트

        // 4. 회원이 결제한 주문을 찾아서 취소 처리
        val orderOptional = orderRepository.findByMemberAndStatus(member, "paid") // 결제 완료된 주문을 조회
        if (orderOptional.isPresent) {
            val order = orderOptional.get()
            order.status = "cancelled" // 주문 상태를 '취소'로 업데이트
            orderRepository.save(order) // 취소된 주문 저장
            println("회원 ${member.username} 의 PREMIUM 구독 취소 처리 완료")
        } else {
            println("결제된 주문을 찾을 수 없습니다. 회원: ${member.username}")
        }
    }
}
