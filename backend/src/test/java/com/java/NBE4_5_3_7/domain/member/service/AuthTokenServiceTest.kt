package com.java.NBE4_5_3_7.domain.member.service

import com.java.NBE4_5_3_7.standard.Ut
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class AuthTokenServiceTest @Autowired constructor(
    private val authTokenService: AuthTokenService
) {

    @Test
    @DisplayName("AuthTokenService 생성")
    fun beanInjectionTest() {
        assertThat(authTokenService).isNotNull()
    }

    @Test
    @DisplayName("JWT 생성 및 페이로드 파싱 테스트")
    fun jwtCreateAndParseTest() {
        // given
        val payload = mapOf("id" to 1L, "username" to "testUser", "nickname" to "테스트계정")

        // when
        val token = Ut.Jwt.createToken(authTokenService.keyString, authTokenService.expireSeconds, payload)

        // then
        assertThat(token).isNotBlank()

        val parsedPayload = Ut.Jwt.getPayload(authTokenService.keyString, token)
        assertThat(parsedPayload["id"]?.toString()?.toLong()).isEqualTo(1L)
        assertThat(parsedPayload["username"]).isEqualTo("testUser")
        assertThat(parsedPayload["nickname"]).isEqualTo("테스트계정")
    }

    @Test
    @DisplayName("Refresh 토큰 생성 및 type이 'refresh'인지 확인")
    fun refreshTokenTest() {
        val payload = mapOf("id" to 1L, "type" to "refresh")

        val refreshToken = Ut.Jwt.createToken(
            authTokenService.keyString,
            authTokenService.refreshExpireSeconds,
            payload
        )

        val parsed = authTokenService.getRefreshPayload(refreshToken)

        assertThat(parsed).isNotNull()
        assertThat(parsed!!["type"]).isEqualTo("refresh")
    }

    @Test
    @DisplayName("유효하지 않은 JWT일 때, null을 반환")
    fun invalidTokenTest() {
        val invalidToken = "this.is.not.a.valid.token"

        val result = authTokenService.getPayload(invalidToken)

        assertThat(result).isNull()
    }

    @Test
    @DisplayName("username, nickname이 없을 때 null처리")
    fun missingFieldsTokenTest() {
        val token = Ut.Jwt.createToken(
            authTokenService.keyString,
            authTokenService.expireSeconds,
            mapOf("id" to 1L) // username, nickname 없음
        )

        val payload = authTokenService.getPayload(token)

        assertThat(payload).isNull()
    }

    @Test
    @DisplayName("JWT 유효성 검증")
    fun jwtValidationTest() {
        val payload = mapOf("id" to 1L, "username" to "tester", "nickname" to "닉네임")
        val token = Ut.Jwt.createToken(authTokenService.keyString, authTokenService.expireSeconds, payload)

        val isValid = Ut.Jwt.isValidToken(authTokenService.keyString, token)
        assertThat(isValid).isTrue()
    }

}
