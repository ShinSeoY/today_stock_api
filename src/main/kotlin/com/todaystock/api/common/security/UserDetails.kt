package com.todaystock.api.common.security

import com.todaystock.api.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetails(
    private val email: String,
    private val member: Member,
    private val roles: List<String> = listOf("USER"),
) : UserDetails {
    fun getMember(): Member = member

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority(it) }
    }

    override fun getPassword(): String = ""

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
