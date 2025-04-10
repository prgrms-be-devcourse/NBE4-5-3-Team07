package com.java.NBE4_5_3_7.domain.openAI

import com.java.NBE4_5_3_7.domain.openAI.dto.ChatRequest
import com.java.NBE4_5_3_7.domain.openAI.dto.ChatResponse
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class OpenAiService(
    webClientBuilder: WebClient.Builder,
) {
    private val webClient: WebClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build()

    @Value("\${openai.api.key}")
    private val apiKey: String? = null

    fun askStartInterview(interviewType: String?): String {
        val systemMessage = when {
            "CS".equals(interviewType, ignoreCase = true) -> {
                Message(
                    "system",
                    "당신은 신입 개발자를 대상으로 CS 지식에 관한 기술 면접을 진행하는 면접관입니다. " +
                            "지원자의 기초 CS 지식과 논리적 사고를 평가할 수 있는 구체적이고 현실적인 질문을 생성하세요. " +
                            "한 주제에 대해서는 최초 질문에 대한 후속(꼬리) 질문을 지원자의 답변을 듣고 하나씩 순차적으로 생성하며, " +
                            "최대 3개까지만 허용됩니다. 만약 지원자의 답변이 충분하다면 꼬리 질문 없이 바로 새로운 주제로 전환해 주세요. " +
                            "이상하거나 부적절한 질문은 절대 하지 마십시오."
                )
            }
            "프로젝트".equals(interviewType, ignoreCase = true) -> {
                Message(
                    "system",
                    "당신은 신입 개발자를 대상으로 프로젝트 경험에 관한 기술 면접을 진행하는 면접관입니다. 면접자의 프로젝트 경험에 대해 면접을 진행합니다. " +
                            "경험했던 프로젝트 주제가 무엇이었고, 해당 프로젝트에서 맡은 역할이 무엇이었나요? 라고 질문하세요. " +
                            "첫 질문은 간단 명료하게 하며, 이상하거나 부적절한 단어를 사용하지 말고, 대화 주제와 상관없는 질문은 하지 마십시오."
                )
            }
            else -> {
                Message("system", "면접을 시작합니다. 구체적이고 현실적인 질문만 생성하세요.")
            }
        }

        val chatRequest = ChatRequest("gpt-3.5-turbo", listOf(systemMessage))
        val chatResponse = sendToOpenAI(chatRequest)

        return extractContent(chatResponse, "초기 질문을 생성하는 중 오류가 발생했습니다.")
    }

    fun askNextInterview(interviewType: String?, answer: String?): String {
        if (answer == null) {
            return "답변이 제공되지 않았습니다. 다시 시도해주세요."
        }

        val systemMessage = when {
            "CS".equals(interviewType, ignoreCase = true) -> {
                Message(
                    "system",
                    "당신은 신입 개발자를 대상으로 CS 지식에 관한 기술 면접을 진행하는 면접관입니다. " +
                            "지금은 지원자의 답변을 바탕으로 후속 질문(꼬리 질문)을 하나 생성해 주세요. " +
                            "최대 3개까지만 허용하며, 답변이 충분하면 새로운 주제로 전환해도 됩니다. " +
                            "질문은 구체적이고 현실적이어야 하며, 이상하거나 엉뚱한 질문은 하지 마십시오."
                )
            }
            "프로젝트".equals(interviewType, ignoreCase = true) -> {
                Message(
                    "system",
                    "면접자의 프로젝트 관련 답변을 참고하여 후속 질문을 하나 생성하세요. " +
                            "해당 기능 구현 중 겪은 어려움과 해결 방안 등을 묻는 구체적 질문이어야 합니다. " +
                            "질문은 3개를 넘지 않아야 하며, 불필요하거나 엉뚱한 질문은 하지 마십시오."
                )
            }
            else -> {
                Message("system", "면접을 진행합니다. 구체적이고 현실적인 후속 질문만 생성하세요.")
            }
        }

        val userMessage = Message(
            "user",
            "지원자의 답변: $answer"
        )
        val chatRequest = ChatRequest("gpt-3.5-turbo", listOf(systemMessage, userMessage))
        val chatResponse = sendToOpenAI(chatRequest)

        return extractContent(chatResponse, "후속 질문을 생성하는 중 오류가 발생했습니다.")
    }

    fun evaluateInterview(conversation: List<Message>?): String {
        if (conversation.isNullOrEmpty()) {
            return "평가할 대화 내용이 없습니다."
        }

        val transcript = StringBuilder()
        for (msg in conversation) {
            transcript.append(msg.role ?: "unknown").append(": ").append(msg.content ?: "").append("\n")
        }

        val prompt = """
            아래는 면접 질문 및 답변 목록입니다:
            
            $transcript
            이 내용을 기반으로 질문별로 부족한 점, 보완할 점, 모범답안을 한국어로 구체적이고 현실적으로 작성해 주세요. 불필요하게 추상적이거나 이상한 표현은 배제해야 합니다.
            """.trimIndent()

        val chatRequest = ChatRequest("gpt-3.5-turbo", listOf(Message("system", prompt)))
        val chatResponse = sendToOpenAI(chatRequest)

        return extractContent(chatResponse, "평가 결과를 생성하는 중 오류가 발생했습니다.")
    }

    @CircuitBreaker(name = "openai", fallbackMethod = "fallbackOpenAi")
    fun sendToOpenAI(chatRequest: ChatRequest): ChatResponse? {
        return webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .bodyValue(chatRequest)
            .retrieve()
            .bodyToMono(ChatResponse::class.java)
            .block() // 동기 호출
    }

    fun fallbackOpenAi(chatRequest: ChatRequest, t: Throwable): ChatResponse {
        val fallbackMessage = Message("system", "현재 OpenAI 호출이 불가능합니다. 잠시 후 다시 시도해주세요.")

        val fallbackChoice = ChatResponse.Choice().apply {
            message = fallbackMessage
        }

        return ChatResponse().apply {
            choices = listOf(fallbackChoice)
        }
    }



//    private fun sendToOpenAI(chatRequest: ChatRequest): ChatResponse? {
//        return try {
//            webClient.post()
//                .uri("/chat/completions")
//                .header("Authorization", "Bearer $apiKey")
//                .header("Content-Type", "application/json")
//                .bodyValue(chatRequest)
//                .retrieve()
//                .bodyToMono(ChatResponse::class.java)
//                .block()
//        } catch (e: Exception) {
//            null
//        }
//    }

    private fun extractContent(chatResponse: ChatResponse?, fallback: String): String {
        return if (chatResponse != null && !chatResponse.choices.isNullOrEmpty()) {
            chatResponse.choices?.get(0)?.message?.content ?: "응답을 받지 못했습니다."
        } else {
            "죄송합니다. $fallback"
        }
    }
}