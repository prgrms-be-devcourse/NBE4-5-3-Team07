package com.java.NBE4_5_3_7.domain.payment

import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.domain.payment.dto.reqestDto.PaymentRequestDto
import io.mockk.impl.annotations.MockK
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.*
import org.mockito.BDDMockito.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PaymentServiceTest {

    @LocalServerPort
    private var port: Int = 0

    private val basePath = "/api/v1/payments"

    @MockBean
    lateinit var memberService: MemberService

    @BeforeEach
    fun setUp() {
        RestAssured.port = port

        val mockMember = Member().apply {
            id = 1L
            username = "test@example.com"
            nickname = "TestUser"
        }
        given(memberService.getMemberFromRq()).willReturn(mockMember)

    }

    @Test
    @Order(1)
    @DisplayName("결제 검증 요청")
    fun testVerifyPayment() {
        val requestDto = PaymentRequestDto().apply {
            imp_uid = "imp_test_123456789"
        }

        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .`when`()
            .post("$basePath/verify")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("impUid", notNullValue())
            .body("merchantUid", equalTo("merchant_test_123456789"))
            .body("status", notNullValue())
    }

    @Test
    @Order(2)
    @DisplayName("구독 취소 테스트")
    fun testCancelSubscription() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .post("$basePath/cancel")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("message", equalTo("구독 취소 성공"))
    }
}
