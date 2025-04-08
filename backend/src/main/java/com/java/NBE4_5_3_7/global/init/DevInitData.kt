package com.java.NBE4_5_3_7.global.init

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Profile("local")
@Configuration
class DevInitData {

    companion object {
        private const val API_URL = "http://localhost:8080/v3/api-docs"
        private const val API_JSON_FILE = "apiV1.json"
    }

    @Bean
    fun devApplicationRunner(): ApplicationRunner = ApplicationRunner {
        generateApiJsonFile()
        executeCommand()
    }

    private fun generateApiJsonFile() {
        val filePath = Path.of(API_JSON_FILE)

        try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() != 200) {
                throw RuntimeException("API 요청 실패: HTTP 상태 코드 ${response.statusCode()}")
            }

            Files.writeString(
                filePath,
                response.body(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
            println("JSON 데이터가 ${filePath.toAbsolutePath()}에 저장되었습니다.")
        } catch (e: IOException) {
            throw RuntimeException("API JSON 파일 생성 중 오류 발생: $API_JSON_FILE", e)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("API JSON 파일 생성 중 인터럽트 발생: $API_JSON_FILE", e)
        }
    }

    private fun executeCommand() {
        val tsGenCommand = getTsGenCommand()
        val processBuilder = ProcessBuilder(tsGenCommand)
        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.lines().forEach { println(it) }
            }

            val exitCode = process.waitFor()
            println("프로세스 종료 코드: $exitCode")
        } catch (e: IOException) {
            throw RuntimeException("명령 실행 중 IO 오류 발생: $tsGenCommand", e)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("명령 실행 중 인터럽트 발생: $tsGenCommand", e)
        }
    }

    private fun getTsGenCommand(): List<String> {
        val os = System.getProperty("os.name").lowercase()
        val command = "npx --package typescript --package openapi-typescript --package punycode openapi-typescript " +
                "$API_JSON_FILE -o ../frontend/src/lib/backend/apiV1/schema.d.ts"

        return if (os.contains("win")) {
            listOf("cmd.exe", "/c", command)
        } else {
            listOf("sh", "-c", command)
        }
    }
}
