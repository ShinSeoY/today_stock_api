package com.todaystock.api.repository

import com.todaystock.api.entity.AuthProvider
import com.todaystock.api.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, String> {
    fun findByEmail(email: String): Member?

    fun findByEmailAndProvider(
        email: String,
        provider: AuthProvider,
    ): Member?
}
