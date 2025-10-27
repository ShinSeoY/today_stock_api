package com.todaystock.api.common.security

import com.todaystock.api.common.utils.JwtUtil
import com.todaystock.api.entity.AuthProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
        private val jwtUtil: JwtUtil,
        private val customUserDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain,
    ) {

        // Todo : ✅ OAuth2 로그인 플로우 전체를 JWT 필터에서 제외
//    val uri = request.requestURI
//    println(uri)
//    if (uri.contains("/oauth2") ||
//        uri.contains("/login")) {
//        println("---come")
//        // SecurityContext 초기화 (기존 인증 무시)
//        SecurityContextHolder.clearContext()
//        filterChain.doFilter(request, response)
//        return
//    }

        try {
            val token = extractToken(request)

            if (!token.isNullOrBlank()) {
                val (email, providerName) = jwtUtil.getEmailAndProviderFromToken(token)
                val customUserDetails = customUserDetailsService.loadUserById(email, AuthProvider.valueOf(providerName))
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val principal = AppPrincipal(customUserDetails.getMember(), attributesDelegate = emptyMap(), authoritiesDelegate = authorities)

                val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)

        } catch (e: Exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    private fun extractToken(req: HttpServletRequest): String? {
        req.getHeader(HttpHeaders.AUTHORIZATION)?.let { h ->
            if (h.startsWith("Bearer ")) return h.removePrefix("Bearer ").trim()
        }
        req.cookies?.firstOrNull { it.name == "ACCESS_TOKEN" }?.value?.let { return it }
        return null
    }
}
