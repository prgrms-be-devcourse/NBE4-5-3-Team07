package com.java.NBE4_5_3_7.domain.mail

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@SpringBootTest
@ActiveProfiles("local")
class EmailServiceTest {

    @Autowired
    lateinit var emailService: EmailService

    @Test
    fun `이메일 발송 TEST`() {
        // given
        val sender = "user@gmail.com"
        val content = "[테스트] 문의합니다."
        val timestamp = Instant.now().toString()

        // when
        emailService.sendChatNotification(sender, content, timestamp)

        // then
        Thread.sleep(5000)
    }
}