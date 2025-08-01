package com.todaystock.api.repository

import com.todaystock.api.entity.Alarm
import com.todaystock.api.entity.AlarmId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlarmRepository : JpaRepository<Alarm, AlarmId> {
}
