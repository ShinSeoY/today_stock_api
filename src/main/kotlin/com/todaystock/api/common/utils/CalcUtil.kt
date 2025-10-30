package com.todaystock.api.common.utils

import com.todaystock.api.entity.AlarmInfo
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class CalcUtil {

    private fun sha256(s: String): String =
            MessageDigest.getInstance("SHA-256")
                    .digest(s.toByteArray())
                    .joinToString("") { "%02x".format(it) }

    fun calcConfigHash(a: AlarmInfo): String {
        val raw = listOf(
                a.code,
                a.requestEmail,
                a.conditionType,
                a.requestPrice
        ).joinToString("|")
        return sha256(raw)
    }
}