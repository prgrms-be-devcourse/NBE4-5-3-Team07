package com.java.NBE4_5_3_7.domain.interview

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewCategory
import com.java.NBE4_5_3_7.domain.interview.entity.InterviewContent
import com.java.NBE4_5_3_7.domain.interview.entity.dto.request.InterviewContentAdminRequestDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.BookmarkResponseDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewContentAdminResponseDto
import com.java.NBE4_5_3_7.domain.interview.entity.dto.response.InterviewResponseDto
import com.java.NBE4_5_3_7.domain.interview.repository.InterviewContentRepository
import com.java.NBE4_5_3_7.domain.interview.service.InterviewService
import com.java.NBE4_5_3_7.domain.member.entity.Role
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.security.SecurityUser
import com.java.NBE4_5_3_7.standard.Ut
import io.restassured.RestAssured
import io.restassured.http.ContentType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*
import kotlin.test.assertTrue

@Import(TestSecurityFilterConfig::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.profiles.active=test"]
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc(addFilters = false)
class InterviewTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var interviewService: InterviewService

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    lateinit var interviewContentRepository: InterviewContentRepository

    lateinit var accessToken: String

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        accessToken = createTestUserAndGetAccessToken()
    }

    private fun createTestUserAndGetAccessToken(): String {
        val uniqueSuffix = UUID.randomUUID().toString().take(8)
        val username = "testUser_$uniqueSuffix"
        val nickname = "테스트계정_$uniqueSuffix"
        val profileImgUrl = "https://example.com/profile.png"

        memberService.join(username, nickname, profileImgUrl)

        // ID가 할당된 실제 member 객체로 토큰 생성
        val saved = memberService.findByUsername(username).get()
        val token = memberService.genAccessToken(saved)

        return "Bearer $token"
    }

    companion object {
        @Container
        val redis = object : GenericContainer<Nothing>("redis:7.0.5") {
            init {
                withExposedPorts(6379)
            }
        }
    }

    @Test
    fun `서버 포트 출력 확인`() {
        println("테스트용 임시 서버 포트: http://localhost:$port")
    }

    @Test
    fun `DB 연결 확인`() {
        val url = environment.getProperty("spring.datasource.url")
        println("🧪 현재 연결된 DB: $url")
        assertThat(url).isNotBlank
    }

    @Test
    @DisplayName("GET /interview/all")
    fun `전체 head ID 목록 조회`() {
        // when
        val response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("http://localhost:$port/interview/all")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        // then
        val result: List<Long> = jacksonObjectMapper().readValue(response)
        println("전체 head ID 리스트: $result")

        assertTrue(result.isNotEmpty(), "결과 리스트가 비어 있습니다.")
    }

    @Test
    @DisplayName("GET /interview/category/{category}")
    fun `카테고리별 head ID 리스트 반환`() {
        // given
        val category = "DATABASE"

        // when
        val response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("http://localhost:$port/interview/category/$category")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        println("응답: $response")

        // then
        val result: List<Long> = jacksonObjectMapper().readValue(response)
        assertThat(result).isNotEmpty
    }

    @Test
    @DisplayName("GET /interview/keyword")
    fun `키워드 리스트 반환`() {
        val response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("http://localhost:$port/interview/keyword")
            .then()
            .statusCode(200)
            .extract().asString()

        println("응답: $response")

        val result = jacksonObjectMapper().readValue(response, Array<String>::class.java)
        assertThat(result).isNotEmpty()
    }

    @Test
    @DisplayName("POST /interview/keyword/content")
    fun `head ID 리스트 반환`() {
        val keywordList = listOf("DBMS", "정규화", "Transaction")
        val request = mapOf("keywordList" to keywordList)

        val response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(jacksonObjectMapper().writeValueAsString(request))
            .`when`()
            .post("http://localhost:$port/interview/keyword/content")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        println("응답: $response")

        val result: List<Long> = jacksonObjectMapper().readValue(response)
        assertThat(result).isNotEmpty
    }

    @Test
    @DisplayName("GET /interview/{id}")
    fun `인증된 사용자로 질문 상세 조회`() {
        val saved = interviewContentRepository.save(
            InterviewContent().apply {
                question = "의존성 주입이란?"
                modelAnswer = "객체의 의존성을 외부에서 주입받는 설계 방식입니다."
                category = InterviewCategory.SPRING
                keyword = "DI"
                isHead = true
                hasTail = false
            }
        )

        val token = accessToken

        val response = RestAssured
            .given()
            .header("Authorization", token)
            .contentType(ContentType.JSON)
            .`when`()
            .get("http://localhost:$port/interview/${saved.interviewContentId}")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        println("응답: $response")

        val result: InterviewResponseDto = jacksonObjectMapper().readValue(response)
        assertThat(result.id).isEqualTo(saved.interviewContentId)
    }

    @Test
    @DisplayName("GET /interview/bookmark")
    fun `인증된 사용자의 북마크 목록 반환`() {
        // given
        val suffix = UUID.randomUUID().toString().take(8)
        val member = memberService.join(
            username = "user_$suffix",
            nickname = "유저$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )

        val token = memberService.genAccessToken(member)
        val savedContent = interviewContentRepository.save(
            InterviewContent().apply {
                question = "트랜잭션이란?"
                modelAnswer = "하나의 논리적 작업 단위로, 모두 성공하거나 모두 실패해야 합니다."
                category = InterviewCategory.DATABASE
                keyword = "transaction"
                isHead = true
                hasTail = false
            }
        )

        interviewService.bookmark(member.id, savedContent.interviewContentId!!)

        // when
        val response = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .`when`()
            .get("http://localhost:$port/interview/bookmark")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        println("북마크 응답: $response")

        // then
        val result = jacksonObjectMapper().readValue(response, Array<BookmarkResponseDto>::class.java)
        assertThat(result).isNotEmpty()
        assertThat(result[0].question).isEqualTo("트랜잭션이란?")
    }

    @Test
    @DisplayName("POST & DELETE /interview/bookmark")
    fun `북마크 등록 후 삭제`() {
        // given: 유저 + 인터뷰 생성
        val suffix = UUID.randomUUID().toString().take(8)
        val member = memberService.join(
            username = "user_$suffix",
            nickname = "유저$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )

        val token = memberService.genAccessToken(member)

        val content = interviewContentRepository.save(
            InterviewContent().apply {
                question = "트랜잭션이란?"
                modelAnswer = "하나의 논리적 작업 단위입니다."
                keyword = "transaction"
                category = InterviewCategory.DATABASE
                isHead = true
                hasTail = false
            }
        )

        // when: 북마크 등록
        val addResponse = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .post("http://localhost:$port/interview/bookmark?id=${content.interviewContentId}")
            .then()
            .statusCode(200)
            .extract().asString()

        println("북마크 등록 응답: $addResponse")
        assertThat(addResponse).contains("등록")

        // when: 북마크 목록 조회
        val listResponse = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .get("http://localhost:$port/interview/bookmark")
            .then()
            .statusCode(200)
            .extract().asString()

        println("북마크 목록 응답: $listResponse")

        val bookmarkList = jacksonObjectMapper().readValue(listResponse, Array<BookmarkResponseDto>::class.java)
        assertThat(bookmarkList).isNotEmpty()

        val bookmarkId = bookmarkList[0].contentId

        // when: 북마크 삭제
        val deleteResponse = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .delete("http://localhost:$port/interview/bookmark/$bookmarkId")
            .then()
            .statusCode(200)
            .extract().asString()

        println("삭제 응답: $deleteResponse")
        assertThat(deleteResponse).contains("삭제")
    }

    @Test
    @DisplayName("GET /interview/like?id")
    fun `인터뷰 좋아요 등록 후 취소`() {
        // given: 사용자 + 토큰
        val suffix = UUID.randomUUID().toString().take(8)
        val member = memberService.join(
            username = "user_$suffix",
            nickname = "유저$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )
        val token = memberService.genAccessToken(member)

        // given: 인터뷰 콘텐츠 생성
        val content = interviewContentRepository.save(
            InterviewContent().apply {
                question = "DB 인덱스란?"
                modelAnswer = "빠른 조회를 위한 구조입니다."
                keyword = "index"
                category = InterviewCategory.DATABASE
                isHead = true
                hasTail = false
            }
        )

        // when: 좋아요 등록
        val added = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .get("http://localhost:$port/interview/like?id=${content.interviewContentId}")
            .then()
            .statusCode(200)
            .extract().asString()

        println("등록 응답: $added")
        assertThat(added).contains("추가")

        // when: 다시 호출 → 좋아요 취소
        val removed = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .get("http://localhost:$port/interview/like?id=${content.interviewContentId}")
            .then()
            .statusCode(200)
            .extract().asString()

        println("취소 응답: $removed")
        assertThat(removed).contains("취소")
    }

    @Test
    @DisplayName("GET /api/v1/admin/interview/all")
    fun `관리자 권한으로 전체 인터뷰 카테고리 조회`() {
        // given
        val suffix = UUID.randomUUID().toString().take(8)
        val member = memberService.join(
            username = "admin_$suffix",
            nickname = "관리자$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )
        member.role = Role.ADMIN
        memberRepository.save(member)
        val token = memberService.genAccessToken(member)

        // when
        val response = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .`when`()
            .get("http://localhost:$port/api/v1/admin/interview/all")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        println("관리자 카테고리 키워드 조회 결과: $response")

        // then
        val result: Map<String, List<String>> = jacksonObjectMapper().readValue(
            response,
            object : TypeReference<Map<String, List<String>>>() {}
        )

        assertThat(result).isNotEmpty
        assertThat(result).containsKeys("DATABASE", "SPRING")
    }


    @Test
    @DisplayName("POST /api/v1/admin/interview - head 등록 후 tail 등록까지")
    fun `관리자 head 질문 등록 후 tail 질문 등록`() {
        // given: 관리자 생성
        val suffix = UUID.randomUUID().toString().take(8)
        val admin = memberService.join(
            username = "admin_$suffix",
            nickname = "관리자$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )
        admin.role = Role.ADMIN
        memberRepository.save(admin)
        val token = memberService.genAccessToken(admin)

        // 1. head 질문 등록
        val headRequest = InterviewContentAdminRequestDto(
            question = "Spring이란?",
            modelAnswer = "자바 기반 프레임워크입니다.",
            category = InterviewCategory.SPRING,
            keyword = "Spring",
            isHead = true,
            headId = null
        )

        val headId = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body(headRequest)
            .post("http://localhost:$port/api/v1/admin/interview")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("id")

        // 2. tail 질문 등록
        val tailRequest = InterviewContentAdminRequestDto(
            question = "DI란?",
            modelAnswer = "의존성 주입입니다.",
            category = InterviewCategory.SPRING,
            keyword = "DI",
            isHead = false,
            headId = headId
        )

        val tailId = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body(tailRequest)
            .post("http://localhost:$port/api/v1/admin/interview")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("id")

        // then: 둘 다 존재하는지 확인
        val head = interviewContentRepository.findById(headId).orElseThrow()
        val tail = interviewContentRepository.findById(tailId).orElseThrow()

        assertThat(head.isHead).isTrue()
        assertThat(head.headId).isNull()

        assertThat(tail.isHead).isFalse()
        assertThat(tail.headId).isEqualTo(head.interviewContentId)
    }

    @Test
    @DisplayName("GET /api/v1/admin/interview/{headId}/related - head와 연결된 모든 질문 조회")
    fun `head 인터뷰에 연결된 tail 포함 전체 관련 질문 조회`() {
        // given: 관리자 계정 및 토큰
        val suffix = UUID.randomUUID().toString().take(8)
        val admin = memberService.join(
            username = "admin_$suffix",
            nickname = "관리자$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )
        admin.role = Role.ADMIN
        memberRepository.save(admin)
        val token = memberService.genAccessToken(admin)

        // 1. head 인터뷰 등록
        val headRequest = InterviewContentAdminRequestDto(
            question = "Spring이란?",
            modelAnswer = "자바 기반 프레임워크입니다.",
            category = InterviewCategory.SPRING,
            keyword = "Spring",
            isHead = true,
            headId = null
        )

        val headId = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body(headRequest)
            .post("http://localhost:$port/api/v1/admin/interview")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("id")

        // 2. tail 인터뷰 1개 등록
        val tailRequest = InterviewContentAdminRequestDto(
            question = "DI란?",
            modelAnswer = "의존성 주입입니다.",
            category = InterviewCategory.SPRING,
            keyword = "DI",
            isHead = false,
            headId = headId
        )

        RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body(tailRequest)
            .post("http://localhost:$port/api/v1/admin/interview")
            .then()
            .statusCode(200)

        // when: head + 관련 tail 리스트 조회
        val response = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .get("http://localhost:$port/api/v1/admin/interview/$headId/related")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        println("연관된 인터뷰 질문 조회 결과: $response")

        // then: 응답 검증
        val result = jacksonObjectMapper().readValue(
            response,
            Array<InterviewContentAdminResponseDto>::class.java
        )

        assertThat(result).hasSize(2) // head + 1 tail

        val head = result.find { it.isHead }
        val tail = result.find { !it.isHead }

        assertThat(head).isNotNull
        assertThat(head!!.question).isEqualTo("Spring이란?")

        assertThat(tail).isNotNull
        assertThat(tail!!.question).isEqualTo("DI란?")
        assertThat(tail.headId).isEqualTo(head.id)
    }

    @Test
    @DisplayName("PUT /api/v1/admin/interview")
    fun `인터뷰 질문 수정`() {
        // given: 관리자 계정 및 토큰
        val suffix = UUID.randomUUID().toString().take(8)
        val admin = memberService.join(
            username = "admin_$suffix",
            nickname = "관리자$suffix",
            profileImgUrl = "https://example.com/profile.png"
        )
        admin.role = Role.ADMIN
        memberRepository.save(admin)
        val token = memberService.genAccessToken(admin)

        // 1. 인터뷰 등록
        val createRequest = InterviewContentAdminRequestDto(
            question = "JPA란?",
            modelAnswer = "자바 ORM 프레임워크입니다.",
            category = InterviewCategory.SPRING,
            keyword = "JPA",
            isHead = true,
            headId = null
        )

        val interviewId = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body(createRequest)
            .post("http://localhost:$port/api/v1/admin/interview")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getLong("id")

        // 2. 수정 요청
        val updateRequest = InterviewContentAdminRequestDto(
            question = "JPA란 무엇인가?",
            modelAnswer = "Java Persistence API입니다.",
            category = InterviewCategory.SPRING,
            keyword = "JPA",
            isHead = true,
            headId = null
        )

        val response = RestAssured
            .given()
            .header("Authorization", "Bearer $token")
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .put("http://localhost:$port/api/v1/admin/interview/$interviewId")
            .then()
            .statusCode(200)
            .extract()
            .asString()

        // then: 응답 검증
        val updated = jacksonObjectMapper().readValue(response, InterviewContentAdminResponseDto::class.java)

        assertThat(updated.id).isEqualTo(interviewId)
        assertThat(updated.question).isEqualTo("JPA란 무엇인가?")
        assertThat(updated.modelAnswer).isEqualTo("Java Persistence API입니다.")
    }
}

@TestConfiguration
class TestSecurityFilterConfig {

    @Value("\${custom.jwt.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun testJwtFilter(): OncePerRequestFilter = object : OncePerRequestFilter() {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            val token = request.getHeader("Authorization")?.removePrefix("Bearer ")?.trim()
            if (token != null) {
                val payload = Ut.Jwt.getPayload(secretKey, token)
                if (payload != null) {
                    val memberId = (payload["id"] as Number).toLong()
                    val username = payload["username"] as String
                    val nickname = payload["nickname"] as String

                    val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                    val principal = SecurityUser(memberId, username, nickname, authorities)
                    val auth = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
            filterChain.doFilter(request, response)
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity, testJwtFilter: OncePerRequestFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .addFilterBefore(testJwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}

