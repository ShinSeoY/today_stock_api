package com.todaystock.api.dto.request

data class AlimRequestDto (
     val code: String,
     val requestEmail: String,
     val price: Double,
     val percent: Double?,
     val condition: String?,
)