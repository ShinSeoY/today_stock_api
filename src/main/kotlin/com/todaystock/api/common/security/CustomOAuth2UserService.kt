package com.todaystock.api.common.security

import com.todaystock.api.entity.AuthProvider
import com.todaystock.api.entity.Member
import com.todaystock.api.entity.MemberId
import com.todaystock.api.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
        private val memberRepository: MemberRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val clientName = userRequest.clientRegistration.registrationId.uppercase() // GOOGLE or KAKAO
        val delegateUser = DefaultOAuth2UserService().loadUser(userRequest)
        val attributes = delegateUser.attributes

        val (email, name, picture, provider) = when (clientName) {
            "GOOGLE" -> {
                val email = attributes["email"] as String
                val name = attributes["name"] as? String
                val picture = attributes["picture"] as? String
                Quadruple(email, name, picture, AuthProvider.GOOGLE)
            }
            "KAKAO" -> {
                val kakaoAccount = attributes["kakao_account"] as Map<*, *>
                val profile = kakaoAccount["profile"] as Map<*, *>
                val email = kakaoAccount["email"] as String
                val name = profile["nickname"] as? String
                val picture = profile["profile_image_url"] as? String
                Quadruple(email, name, picture, AuthProvider.KAKAO)
            }
            else -> throw IllegalArgumentException("Unsupported provider: $clientName")
        }

        val member = memberRepository.findByMemberId(MemberId(email, provider))
                ?: memberRepository.save(Member(memberId = MemberId(email, provider), name = name, picture = picture))

        val normalized = buildMap<String, Any> {
            putAll(attributes) // 원본도 유지
            put("email", member.memberId.email)
            put("name", member.name ?: name ?: "")
            picture?.let { put("picture", it) }
            put("provider", provider.name)
        }

        // ROLE_USER 기본 부여
        val authorities = delegateUser.authorities.takeIf { it.isNotEmpty() }
                ?: listOf(SimpleGrantedAuthority("ROLE_USER"))

        return AppPrincipal(
                member = member,
                attributesDelegate = normalized,
                authoritiesDelegate = authorities
        )
    }

    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
