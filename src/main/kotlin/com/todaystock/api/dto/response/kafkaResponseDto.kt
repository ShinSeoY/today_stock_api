package com.todaystock.api.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties
data class SuccessResponseDto(
        val memberProvider: String,
        val memberEmail: String,
        val code: String,
        val redisKey: String?,
        val lockToken: String?
)
