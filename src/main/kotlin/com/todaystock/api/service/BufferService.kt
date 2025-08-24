package com.todaystock.api.service

import com.todaystock.api.dto.response.SuccessResponseDto
import com.todaystock.api.entity.AlarmId
import com.todaystock.api.entity.AuthProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class BufferService(
    private val memberService: MemberService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun flushBuffer(
        buffer: MutableList<SuccessResponseDto>,
        batchSize: Int,
    ) {
        val successResponseBufferCopy =
            synchronized(buffer) {
                val copy = buffer.toList()
                buffer.clear()
                copy
            }

        runCatching {
            bulkUpdate(
                successResponseBufferCopy.map {
                    AlarmId(
                        memberEmail = it.memberEmail,
                        memberProvider = AuthProvider.valueOf(it.memberProvider),
                        code = it.code,
                    )
                },
                batchSize = batchSize,
            )
            logger.info("Successfully saved data: ${successResponseBufferCopy.size}")
        }.onFailure {
            logger.error("Failed to save to DB.", it)
        }
    }

    private fun bulkUpdate(
        alarmIds: List<AlarmId>,
        batchSize: Int,
    ) {
        if (alarmIds.size <= batchSize) {
            memberService.bulkUpdateAlarmStatus(alarmIds)
        } else {
            alarmIds.chunked(batchSize).forEach { chunk ->
                memberService.bulkUpdateAlarmStatus(chunk)
            }
        }
    }
}
