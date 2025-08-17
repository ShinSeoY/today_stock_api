package com.todaystock.api.dto.response

data class KafkaResponseDto(
        val memberProvider: String? = null,
        val memberEmail: String? = null,
        val requestEmail: String? = null,
        val name: String? = null,
        val requestUrl: String? = null,
        val requestPrice: String? = null,
        val collectedPrice: String? = null,
        val condition: String? = null,
        val conditionType: String? = null,
        val crawled: Boolean? = null,
        val emailed: Boolean? = null,
        val success: Boolean? = null,
        val errors: List<Map<String, Any?>>? = null
)
