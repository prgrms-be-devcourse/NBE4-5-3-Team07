package com.java.NBE4_5_3_7.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        // "CookieAuth" 이름으로 보안 스키마를 추가합니다.
        val cookieAuthScheme = SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.COOKIE)
            .name("access_token")

        return OpenAPI()
            .components(Components().addSecuritySchemes("CookieAuth", cookieAuthScheme))
            .addSecurityItem(SecurityRequirement().addList("CookieAuth"))
    }
}
