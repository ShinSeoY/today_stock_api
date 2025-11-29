package com.todaystock.api.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class SuccessResponseDto(
        val memberProvider: String,
        val memberEmail: String,
        val code: String,
        val emailed: Boolean,
        val configHash: String,
)

@JsonIgnoreProperties
data class FailedResponseDto(
        val requestUrl: String,
        val requestEmail: String,
        val requestPrice: String,
        val conditionType: String,
        val memberProvider: String,
        val memberEmail: String,
        val configHash: String,
        val retryCount: Int,
        val emailed: Boolean,
        val errors: List<Map<String, String>>,
        val stage: String,
        val crawled: Boolean,
        val collectedPrice: String,
        val success: Boolean
)