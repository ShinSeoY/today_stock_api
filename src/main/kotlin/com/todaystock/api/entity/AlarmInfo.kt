package com.todaystock.api.entity

data class AlarmInfo(
        val memberProvider: String,
        val memberEmail: String,
        val requestEmail: String,
        val name: String,
        val conditionType: ConditionType,
        val requestUrl: String,
        val requestPrice: String,
        val code: String,
        var configHash: String? = null,
)
