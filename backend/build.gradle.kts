plugins {
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.java"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://jitpack.io") }
}

dependencies {

	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

	implementation("com.github.iamport:iamport-rest-client-java:0.2.23")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter")

	// DB
	runtimeOnly("com.mysql:mysql-connector-j")

	// 개발 도구
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// CSV 처리
	implementation("com.opencsv:opencsv:5.7.1")

	// OpenAI API 연동
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Redis
	implementation("org.redisson:redisson-spring-boot-starter:3.16.3")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.data:spring-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	// 이메일
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	// Kotlin 표준 라이브러리
	implementation(kotlin("stdlib"))

	// 테스트
	testImplementation(kotlin("test"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core")
	testImplementation("io.mockk:mockk:1.13.10")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.testcontainers:junit-jupiter:1.19.0")
	testImplementation("org.testcontainers:postgresql:1.19.0")
	testImplementation("org.testcontainers:mysql:1.19.3")
	testImplementation("io.rest-assured:rest-assured:5.3.1")

	testImplementation("com.h2database:h2")

	testImplementation("io.rest-assured:kotlin-extensions:5.4.0")
	testImplementation("org.testcontainers:junit-jupiter:1.19.1")
	testImplementation("org.testcontainers:redis:1.19.5")
	testImplementation("org.hamcrest:hamcrest:2.2")

	// WebSocket 테스트
	testImplementation("org.springframework:spring-messaging")
	testImplementation("org.springframework.boot:spring-boot-starter-websocket")
	testImplementation("org.java-websocket:Java-WebSocket:1.5.3")
}

tasks.test {
	useJUnitPlatform()
}
