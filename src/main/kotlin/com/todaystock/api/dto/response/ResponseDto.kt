package com.todaystock.api.dto.response

import com.todaystock.api.entity.ConditionType
import java.time.LocalDateTime

data class SearchResponseDto(
    val code: String,
    val name: String,
    val url: String,
)
data class DetailResponseDto(
    val code: String,
    val name: String,
    val price: Double,
)

data class AlarmResponseDto(
        val code: String,
        val name: String,
        val price: Double,
        val condition: ConditionType,
        val email: String,
        val date: LocalDateTime,
)