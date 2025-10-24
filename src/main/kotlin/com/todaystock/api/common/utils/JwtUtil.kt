package com.todaystock.api.common.utils

import com.todaystock.api.entity.AuthProvider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
        @Value("\${jwt.secret}") private val secretB64: String
) {
    private val key = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secretB64))
    private val expirationMs = 86_400_000 // 24시간

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
