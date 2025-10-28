package com.todaystock.api.common.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2FailureHandler : AuthenticationFailureHandler {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
            request: HttpServletRequest,
            response: HttpServletResponse,
            exception: AuthenticationException
    ) {
        // 세션 무효화
        try {
            request.session?.invalidate()
        } catch (_: Exception) {
        }

        // 삭제할 쿠키들 명시적으로 삭제 (JSESSIONID 등)
        val cookiesToDelete = listOf("JSESSIONID", "ACCESS_TOKEN", "REFRESH_TOKEN")
        cookiesToDelete.forEach { name ->
            val c = Cookie(name, "")
            c.path = "/"
            c.maxAge = 0
            // 도메인/secure 설정이 필요하면 추가: c.isHttpOnly = true; c.secure = true
            response.addCookie(c)
        }

        // 새 인가 요청으로 리다이렉트
        // prompt=select_account를 넣어 항상 구글 계정 선택창을 띄움
        val target = "/oauth2/authorization/google?prompt=" +
                URLEncoder.encode("select_account", StandardCharsets.UTF_8)
        response.status = HttpServletResponse.SC_FOUND
        response.setHeader("Location", target)
    }
}
