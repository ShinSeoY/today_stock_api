package com.todaystock.api.common.config

import com.todaystock.api.common.security.CustomOAuth2UserService
import com.todaystock.api.common.security.CustomUserDetailsService
import com.todaystock.api.common.security.JwtAuthenticationFilter
import com.todaystock.api.common.security.OAuth2SuccessHandler
import com.todaystock.api.common.utils.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val oauth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val jwtUtil: JwtUtil,
    private val customUserDetailsService: CustomUserDetailsService,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {
                it.userInfoEndpoint { userInfo ->
                    userInfo.userService(oauth2UserService)
                }.successHandler(oAuth2SuccessHandler)
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtUtil, customUserDetailsService), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
