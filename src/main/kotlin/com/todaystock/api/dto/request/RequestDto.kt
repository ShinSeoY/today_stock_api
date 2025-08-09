package com.todaystock.api.dto.request

data class SearchRequestDto(
        val keyword: String? = null,
        val page: Int? = 1,
        val url: String? = null,
)

data class AlimRequestDto(
        val stock: Stock,
        val requestEmail: String,
        val currentPrice: Double,
        val requestPrice: Double?,
        val percent: Double?,
        val condition: String,
)

data class Stock(
        val code: String,
        val url : String
)