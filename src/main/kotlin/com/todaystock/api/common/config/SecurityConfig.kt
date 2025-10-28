package com.todaystock.api.common.config

import com.todaystock.api.common.security.*
import com.todaystock.api.common.utils.JwtUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
        private val oauth2UserService: CustomOAuth2UserService,
        private val oAuth2SuccessHandler: OAuth2SuccessHandler,
        private val jwtUtil: JwtUtil,
        private val customUserDetailsService: CustomUserDetailsService,
        private val oAuth2FailureHandler: OAuth2FailureHandler
) {
    @Bean
    fun filterChain(
            http: HttpSecurity,
            corsConfigurationSource: CorsConfigurationSource,
    ): SecurityFilterChain {
        http
                .csrf { it.disable() }
                .cors { it.configurationSource(corsConfigurationSource) }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authorizeHttpRequests {
                    it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    it.requestMatchers("/", "/login", "/error", "/css/**", "/js/**").permitAll()
                    it.requestMatchers("/v1/external", "/v1/external/**").permitAll()
                    it.requestMatchers("/actuator/**").permitAll() // kube pod health check
                    it.anyRequest().authenticated()
                }
                .exceptionHandling {
                    it.authenticationEntryPoint { req, res, ex ->
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    }
                    it.accessDeniedHandler(AccessDeniedHandler { req, res, ex ->
                        res.sendError(403)
                    })
                }
                .oauth2Login {
                    it.userInfoEndpoint { userInfo -> userInfo.userService(oauth2UserService) }
                            .successHandler(oAuth2SuccessHandler)
                    it.failureHandler(oAuth2FailureHandler)
                }
                .addFilterBefore(
                        JwtAuthenticationFilter(jwtUtil, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter::class.java
                )

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(
            @Value("\${app.cors.allowed-origins:}") origins: String,
    ): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = origins.split(",").map { it.trim() }
        config.allowedOriginPatterns = listOf("http://localhost:*")

        config.addAllowedHeader(CorsConfiguration.ALL)
        config.addAllowedMethod(CorsConfiguration.ALL)
        config.maxAge = 3600

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
