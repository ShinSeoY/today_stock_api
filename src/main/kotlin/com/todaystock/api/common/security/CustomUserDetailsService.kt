package com.todaystock.api.common.security

import com.todaystock.api.common.exception.ErrorCode
import com.todaystock.api.entity.AuthProvider
import com.todaystock.api.entity.MemberId
import com.todaystock.api.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val member =
            memberRepository.findByMemberIdEmail(username)
                ?: throw UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.message)
        return UserDetails(
            email = member.memberId.email,
            member = member,
        )
    }

    fun loadUserById(
        email: String,
        provider: AuthProvider,
    ): UserDetails {
        val memberId = MemberId(email = email, provider = provider)
        val member =
            memberRepository.findById(memberId)
                .orElseThrow { UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.message) }

        return UserDetails(
            email = memberId.email,
            member = member,
        )
    }
}
