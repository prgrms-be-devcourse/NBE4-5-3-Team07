package com.java.NBE4_5_3_7.domain.chat.controller

import com.java.NBE4_5_3_7.domain.chat.model.Message
import com.java.NBE4_5_3_7.domain.chat.service.ChatService
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.Role
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.profiles.active=local"])
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatControllerTest {

    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var memberService: MemberService

    companion object {
        @Container
        val redis = object : GenericContainer<Nothing>("redis:7.0.5") {
            init {
                withExposedPorts(6379)
            }
        }
    }

    @LocalServerPort
    private var port: Int = 0

    @BeforeAll
    fun setupRoomAndMessage() {
        chatService.saveMessage(999, "GUEST", "테스트메시지1", System.currentTimeMillis().toString())
        chatService.saveMessage(998, "GUEST", "테스트메시지2", System.currentTimeMillis().toString())
        chatService.saveMessage(997, "GUEST", "테스트메시지3", System.currentTimeMillis().toString())
        chatService.saveMessage(996, "GUEST", "테스트메시지4", System.currentTimeMillis().toString())
    }

    @AfterAll
    fun tearDownRoomAndMessage() {
        listOf(999L, 998L, 997L, 996L).forEach {
            chatService.deleteChatRoomMessages(it)
        }
    }

    @Test
    fun `채팅방 정보 조회 성공 TEST`() {
        RestAssured
            .given()
                .contentType(ContentType.JSON)
            .`when`()
                .get("/chat/room/info")
            .then()
                .statusCode(200)
                .body("roomId", notNullValue())
    }

    @Test
    fun `모든 메시지 조회 TEST`() {
        RestAssured
            .given()
                .contentType(ContentType.JSON)
            .`when`()
                .get("/chat/messages/all")
            .then()
                .statusCode(200)
                .body("$", isA<List<*>>(List::class.java))
    }

    @Test
    fun `특정 채팅방 메시지 조회 성공 TEST`() {
        val roomId = 1L

        RestAssured
            .given()
                .contentType(ContentType.JSON)
            .`when`()
                .get("/chat/messages/$roomId")
            .then()
                .statusCode(200)
                .body("$", isA<List<*>>(List::class.java))
    }

    @Test
    fun `채팅방 목록 조회 성공 TEST`() {
        RestAssured
            .given()
                .contentType(ContentType.JSON)
            .`when`()
                .get("/chat/rooms")
            .then()
                .statusCode(200)
                .body("$", isA<List<*>>(List::class.java))
    }

    @Test
    fun `채팅방 삭제 성공 TEST`() {
        val roomId = 1L

        RestAssured
            .given()
                .contentType(ContentType.JSON)
            .`when`()
                .delete("/chat/messages/$roomId")
            .then()
                .statusCode(200)
    }

    @Test
    fun `WebSocket 유저 메시지 전송 및 수신 성공 TEST`() {
        val latch = CountDownLatch(1)

        val transport = listOf(WebSocketTransport(StandardWebSocketClient()))
        val client = WebSocketStompClient(SockJsClient(transport))
        client.messageConverter = MappingJackson2MessageConverter()

        val url = "ws://localhost:$port/ws/chat"
        val roomId = 999L

        val session = client.connectAsync(url, object : StompSessionHandlerAdapter() {
            override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
                println("✅ Connected to WebSocket!")
            }
        }).get(5, TimeUnit.SECONDS)

        session.subscribe("/topic/chat/$roomId", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = Message::class.java

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                val received = payload as Message
                println("수신된 메시지: $received")
                latch.countDown()
            }
        })

        val testMessage = Message(
            roomId = roomId,
            sender = "USER",
            content = "WebSocket 최신 방식 테스트!",
            timestamp = System.currentTimeMillis().toString()
        )

        session.send("/app/chat/user/$roomId", testMessage)

        assertTrue(latch.await(5, TimeUnit.SECONDS))
    }
}