package com.todaystock.api.repository

import com.todaystock.api.entity.Alarm
import com.todaystock.api.entity.AlarmId
import com.todaystock.api.entity.AuthProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlarmRepository : JpaRepository<Alarm, AlarmId> {
    fun findAllByMember_MemberId_EmailAndMember_MemberId_Provider(
            email: String,
            provider: AuthProvider
    ): List<Alarm>
}
