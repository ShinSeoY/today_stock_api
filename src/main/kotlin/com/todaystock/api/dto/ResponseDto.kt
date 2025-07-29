package com.todaystock.api.dto

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