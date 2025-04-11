package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.global.app.AppConfig
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import kotlin.math.log

@Configuration
@Profile("!test")
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val customAuthorizationRequestResolver: CustomAuthorizationRequestResolver,
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/v1/admin/**",
                        "/app/chat/admin/**",
                        "/chat/admin/**"
                    ).hasRole("ADMIN")
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/member/**",
                        "/interview/**",
                        "/ws/**",
                        "/chat/**",
                        "/app/**",
                        "/api/**",
                        "/actuator/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }
            .anonymous { it.disable() }
            .logout { logout ->
                logout
                    .logoutUrl("/member/logout")
                    // POST 메소드 사용으로 변경
                    .logoutRequestMatcher(AntPathRequestMatcher("/member/logout", "POST"))
                    .invalidateHttpSession(true)
                    .deleteCookies("accessToken", "apiKey", "refreshToken", "JSESSIONID")
                    .logoutSuccessHandler { request, response, _ ->
                        val cookieNames = listOf("accessToken", "refreshToken", "apiKey", "JSESSIONID")
                        for (name in cookieNames) {
                            val cookie = Cookie(name, null).apply {
                                path = "/"
                                maxAge = 0
                                isHttpOnly = true
                                secure = true
                                // SameSite 설정 통일
                                setAttribute("SameSite", "Strict")

                                val serverName = request.serverName
                                if (!serverName.equals("localhost", ignoreCase = true)) {
                                    domain = "www.devprep.shop"
                                }
                            }
                            response.addCookie(cookie)
                        }
                        response.status = HttpServletResponse.SC_OK
                    }
            }
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { _, response, _ ->
                        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")
                    }
                    .accessDeniedHandler { _, response, accessDeniedException ->
                        response.sendError(HttpStatus.FORBIDDEN.value(), accessDeniedException.message)
                    }
            }
            .oauth2Login { oauth2 ->
                oauth2.authorizationEndpoint { endpoint ->
                    endpoint.authorizationRequestResolver(customAuthorizationRequestResolver)
                }
                oauth2.successHandler(customAuthenticationSuccessHandler)
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(AppConfig.SITE_FRONT_URL)
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
