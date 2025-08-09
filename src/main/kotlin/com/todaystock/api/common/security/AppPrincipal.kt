package com.todaystock.api.common.security

import com.todaystock.api.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * 컨트롤러에서 @CurrentUser(expression = "member")로 바로 Member를 꺼낼 수 있도록
 * member 필드를 품은 OAuth2User 구현체
 */
class AppPrincipal(
        val member: Member,
        private val attributesDelegate: Map<String, Any>,
        private val authoritiesDelegate: Collection<GrantedAuthority>,
) : OAuth2User {

    override fun getAttributes(): Map<String, Any> = attributesDelegate
    override fun getAuthorities(): Collection<GrantedAuthority> = authoritiesDelegate
    override fun getName(): String = member.memberId.email
}
