package com.todaystock.api.repository

import com.todaystock.api.entity.Alarm
import com.todaystock.api.entity.AlarmId
import com.todaystock.api.entity.AuthProvider
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AlarmRepository : JpaRepository<Alarm, AlarmId> {
    fun findAllByMember_MemberId_EmailAndMember_MemberId_Provider(
            email: String,
            provider: AuthProvider,
    ): List<Alarm>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
            """
        UPDATE Alarm a 
           SET a.enable = :enable
         WHERE a.alarmId IN :ids
    """,
    )
    fun bulkUpdateEnableByIds(
            enable: Boolean,
            ids: List<AlarmId>,
    ): Int

}
