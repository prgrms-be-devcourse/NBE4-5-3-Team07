package com.java.NBE4_5_3_7.domain.mail

import jakarta.mail.MessagingException
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    @Value("\${mail.username}") private val fromEmail: String,
    @Value("\${admin.email}") private val adminEmail: String,
) {

    @Async("asyncExecutor")
    fun sendChatNotification(sender: String, messageContent: String?, timestamp: String) {
        println("📥 sendChatNotification() 호출됨: $sender")
        if ("ADMIN" == sender || "SYSTEM" == sender) return

        val formattedKSTTimestamp = convertToKST(timestamp)

        CompletableFuture.runAsync({
            try {
                TimeUnit.SECONDS.sleep(2)

                val context = Context().apply {
                    setVariable("sender", sender)
                    setVariable("content", messageContent)
                    setVariable("timestamp", formattedKSTTimestamp)
                }

                val htmlBody = templateEngine.process("email-template", context)

                val mimeMessage = mailSender.createMimeMessage()
                val messageHelper = MimeMessageHelper(mimeMessage, true)
                messageHelper.setFrom(fromEmail)
                messageHelper.setTo(adminEmail)
                messageHelper.setSubject("[DevPrep] 새로운 고객센터 문의가 도착했습니다.")
                messageHelper.setText(htmlBody, true)

                mailSender.send(mimeMessage)
            } catch (e: MessagingException) {
                throw RuntimeException(e)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        })
    }

    private fun convertToKST(isoTimestamp: String): String? {
        try {
            val instant = Instant.parse(isoTimestamp)
            val kstDateTime = instant.atZone(ZoneId.of("Asia/Seoul"))
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm")
                .withZone(ZoneId.of("Asia/Seoul"))
            return kstDateTime.format(formatter)
        } catch (e: Exception) {
            return isoTimestamp
        }
    }
}