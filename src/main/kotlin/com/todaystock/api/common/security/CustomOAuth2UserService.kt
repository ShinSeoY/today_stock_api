package com.todaystock.api.common.security

import com.todaystock.api.entity.Member
import com.todaystock.api.repository.MemberRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val memberRepository: MemberRepository,
) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val attributes = oAuth2User.attributes

        val email = attributes["email"] as String
        val name = attributes["name"] as String?
        val picture = attributes["picture"] as String?

        val member =
            memberRepository.findByEmail(email)
                ?: memberRepository.save(Member(email = email, name = name, picture = picture))

        return DefaultOAuth2User(
            setOf(),
            mapOf("id" to member.id, "email" to member.email),
            "email",
        )
    }
}
