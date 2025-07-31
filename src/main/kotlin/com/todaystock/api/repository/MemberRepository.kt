package com.todaystock.api.repository

import com.todaystock.api.entity.Member
import com.todaystock.api.entity.MemberId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, MemberId> {
    fun findByMemberId(memberId: MemberId): Member?

    fun findByMemberIdEmail(email: String): Member?
}
