package com.todaystock.api.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.todaystock.api.entity.ConditionType

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
        val calcPrice: Double?,
)

data class Stock(
        val code: String,
        val url: String,
        val name: String,
        val currencyCode: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmailDto(
        val name: String,
        val conditionType: ConditionType,
        val requestEmail: String,
        val requestPrice: Double,
        val collectedPrice: Double,
)
