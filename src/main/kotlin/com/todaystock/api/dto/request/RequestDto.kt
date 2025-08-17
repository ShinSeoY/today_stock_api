package com.todaystock.api.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.todaystock.api.entity.ConditionType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

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
        val url : String,
        val name: String,
        val currencyCode: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EmailDto(
        val name: String,
        val conditionType: ConditionType,
        val requestEmail: String,
        val requestPrice: Double,
        val collectedPrice: Double
)

data class SendMailRequest(
        @field:Email @field:NotBlank
        val to: String,
        @field:NotBlank
        val subject: String,
        @field:NotBlank
        val body: String,
        val html: Boolean = false
)

data class SendMailWithAttachmentRequest(
        @field:Email @field:NotBlank
        val to: String,
        @field:NotBlank
        val subject: String,
        @field:NotBlank
        val body: String,
        val html: Boolean = false,
        // base64 인라인 첨부 예시
        val filename: String? = null,
        val base64Content: String? = null,
        val contentType: String? = null
)
