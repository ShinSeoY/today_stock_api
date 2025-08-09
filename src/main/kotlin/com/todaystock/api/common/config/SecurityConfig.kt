package com.todaystock.api.common.config

import com.todaystock.api.common.security.*
import com.todaystock.api.common.utils.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
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
) {

    @Bean
    fun filterChain(
            http: HttpSecurity,
            corsConfigurationSource: CorsConfigurationSource,
    ): SecurityFilterChain {
        http
                .cors { it.configurationSource(corsConfigurationSource) }
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    it.requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                            .anyRequest().authenticated()
                }
                .oauth2Login {
                    it.userInfoEndpoint { userInfo -> userInfo.userService(oauth2UserService) }
                            .successHandler(oAuth2SuccessHandler)
                }
                .addFilterBefore(
                        JwtAuthenticationFilter(jwtUtil, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter::class.java
                )

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(
            @Value("\${app.cors.allowed-origins:http://localhost:5173}") origins: String
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
