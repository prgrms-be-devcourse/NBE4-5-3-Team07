package com.java.nbe4_5_3_7.domain.mail

import com.java.NBE4_5_3_7.domain.mail.EmailService
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.TaskScheduler
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.time.Instant
import java.util.concurrent.ScheduledFuture

@ExtendWith(MockitoExtension::class)
class EmailServiceTest {

    private val mailSender = mock(JavaMailSender::class.java)
    private val templateEngine = mock(SpringTemplateEngine::class.java)
    private val taskScheduler = mock(TaskScheduler::class.java)
    private val mimeMessage = mock(MimeMessage::class.java)

    private val emailService = EmailService(
        mailSender = mailSender,
        templateEngine = templateEngine,
        taskScheduler = taskScheduler,
        fromEmail = "devprep.team@gmail.com",
        adminEmail = "devprep.no.reply@gmail.com"
    )

    @Test
    fun `sendChatNotification should render template with correct variables`() {
        // Given
        val sender = "USER"
        val content = "문의 내용입니다."
        val timestamp = Instant.now().toString()

        `when`(mailSender.createMimeMessage()).thenReturn(mimeMessage)
        `when`(templateEngine.process(eq("email-template"), any(Context::class.java))).thenReturn("<html>dummy</html>")
        `when`(taskScheduler.schedule(any(Runnable::class.java), any(Instant::class.java))).thenAnswer { invocation ->
            val runnable = invocation.getArgument<Runnable>(0)
            runnable.run()
            mock(ScheduledFuture::class.java)
        }

        // When
        emailService.sendChatNotification(sender, content, timestamp)

        // Then
        val contextCaptor = argumentCaptor<Context>()
        verify(templateEngine).process(eq("email-template"), contextCaptor.capture())

        val capturedContext = contextCaptor.firstValue
        assert(capturedContext.getVariable("sender") == sender)
        assert(capturedContext.getVariable("content") == content)
        assert((capturedContext.getVariable("timestamp") as String).isNotBlank())
    }

    @Test
    fun `sendChatNotification should not send email when sender is ADMIN`() {
        emailService.sendChatNotification("ADMIN", "관리자 메시지", Instant.now().toString())
        verifyNoInteractions(taskScheduler)
    }

    @Test
    fun `sendChatNotification should not send email when sender is SYSTEM`() {
        emailService.sendChatNotification("SYSTEM", "시스템 메시지", Instant.now().toString())
        verifyNoInteractions(taskScheduler)
    }
}