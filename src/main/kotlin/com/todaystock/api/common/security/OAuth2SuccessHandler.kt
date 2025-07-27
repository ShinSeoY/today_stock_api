package com.todaystock.api.common.security

import com.todaystock.api.common.utils.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(
    private val jwtUtil: JwtUtil,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val oauth2User = authentication.principal as org.springframework.security.oauth2.core.user.OAuth2User
        val email = oauth2User.getAttribute<String>("email") ?: "unknown@example.com"
        val token = jwtUtil.generateToken(email)

        // Vue 페이지로 토큰 전달
        response.sendRedirect("http://localhost:5173/stock-alert?token=$token")
    }
}
