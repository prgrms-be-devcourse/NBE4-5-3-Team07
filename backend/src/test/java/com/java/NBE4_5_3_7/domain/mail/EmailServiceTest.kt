package com.java.NBE4_5_3_7.domain.mail

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest
class EmailServiceTest {

    @Autowired
    lateinit var emailService: EmailService

    @Test
    fun `이메일 발송 TEST`() {
        val sender = "user@gmail.com"
        val content = "[테스트] 문의합니다."
        val timestamp = Instant.now().toString()

        emailService.sendChatNotification(sender, content, timestamp)

        Thread.sleep(3000)
    }
}