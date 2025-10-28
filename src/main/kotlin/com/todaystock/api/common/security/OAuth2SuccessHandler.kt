package com.todaystock.api.common.security

import com.todaystock.api.common.utils.JwtUtil
import com.todaystock.api.entity.AuthProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(
        @Value("\${app.frontend.base-url:}")
        private val frontendBaseUrl: String,
        @Value("\${app.cookie.secure:false}")     // 로컬: false, 배포: true
        private val cookieSecure: Boolean,
        @Value("\${app.cookie.same-site:Lax}")   // cross-site면 None
        private val cookieSameSite: String,
        private val jwtUtil: JwtUtil,
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(req: HttpServletRequest, res: HttpServletResponse, auth: Authentication) {
        val oauth = auth as OAuth2AuthenticationToken
        val oAuth2User = auth.principal as OAuth2User

        val email = oAuth2User.getAttribute<String>("email") ?: error("email missing from provider")
        val provider = AuthProvider.valueOf(oauth.authorizedClientRegistrationId.uppercase())

        // 토큰 생성
        val token = jwtUtil.generateToken(email, provider)
        val maxAge = (jwtUtil.expirationMs / 1000)
        val sameSite = cookieSameSite  // 로컬: Lax / 크로스사이트-HTTPS: None
        val secureAttr = if (cookieSecure) "Secure; " else ""

        // (필요 시 Domain=.example.com 추가)
        val setCookie =
                "ACCESS_TOKEN=$token; Path=/; HttpOnly; Max-Age=$maxAge; ${secureAttr}SameSite=$sameSite"

        res.setHeader("Set-Cookie", setCookie)
        res.sendRedirect("$frontendBaseUrl/stock-alert")
    }

}
