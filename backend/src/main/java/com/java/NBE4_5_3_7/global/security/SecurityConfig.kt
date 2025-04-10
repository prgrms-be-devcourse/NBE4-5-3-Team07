package com.java.NBE4_5_3_7.global.security

import com.java.NBE4_5_3_7.global.app.AppConfig
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
                    .logoutRequestMatcher(AntPathRequestMatcher("/member/logout", "DELETE"))
                    .invalidateHttpSession(true)
                    .deleteCookies("accessToken", "apiKey", "refreshToken", "JSESSIONID")
                    .logoutSuccessHandler { _, response, _ ->
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
