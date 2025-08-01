package com.todaystock.api.entity

data class AlarmInfo (
    val memberProvider: String,
    val memberEmail: String,
    val requestEmail: String,
    val requestUrl: String,
    val requestPrice: String,
)