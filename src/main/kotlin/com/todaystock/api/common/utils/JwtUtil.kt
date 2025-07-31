package com.todaystock.api.common.utils

import com.todaystock.api.entity.AuthProvider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val expirationMs = 3600_000 // 1시간

    fun generateToken(
        email: String,
        provider: AuthProvider,
    ): String {
        return Jwts.builder()
            .setSubject(email)
            .claim("provider", provider.name)
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact()
    }

    fun getEmailAndProviderFromToken(token: String): Pair<String, String> {
        val claims =
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

        val email = claims.subject
        val provider = claims["provider"] as String
        return email to provider
    }
}
