package com.todaystock.api.service

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todaystock.api.common.kafka.KafkaProducer
import com.todaystock.api.dto.request.EmailDto
import com.todaystock.api.entity.AlarmInfo
import com.todaystock.api.entity.ConditionType
import com.todaystock.api.repository.RedisRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class ExternalService(
        private val redisRepository: RedisRepository,
        private val kafkaProducer: KafkaProducer,
        private val mailService: MailService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun sha256(s: String): String =
            MessageDigest.getInstance("SHA-256")
                    .digest(s.toByteArray())
                    .joinToString("") { "%02x".format(it) }

    private fun calcConfigHash(a: AlarmInfo): String {
        val raw = listOf(
                a.requestEmail,
                a.conditionType,
                a.requestPrice
        ).joinToString("|")
        return sha256(raw)
    }

    fun publishAlarms() {
        val entries = redisRepository.findEntriesByPrefix("todaystock:", AlarmInfo::class.java)
        if (entries.isEmpty()) return

        val objectMapper = jacksonObjectMapper()

        entries.forEach { (key, alarm) ->
            val lockKey = "$key:lock"
            val configHash = calcConfigHash(alarm)

            val curr = redisRepository.get(lockKey)
            // 동일한 값은 스킵
            if (curr == configHash) {
                return@forEach
            }

            // 메시지에 configHash 필드 추가
            runCatching {
                redisRepository.save(lockKey, configHash)
                kafkaProducer.sendMessages(objectMapper.writeValueAsString(
                        objectMapper.convertValue<Map<String, Any?>>(alarm) + ("configHash" to configHash)
                ))
            }.onFailure { e ->
                // 발행 실패 시 락 해제(다음 사이클 재시도)
                redisRepository.delete(lockKey)
                logger.error("Kafka send failed for key=$key", e)
            }
        }
    }

    @Async
    fun sendEmail(dto: EmailDto) {
        val subject = "오늘의 주식 알림"
        val body =
                """
            종목명 : ${dto.name} 
            설정가 : ${dto.requestPrice} [ ${if (dto.conditionType == ConditionType.GTE) "이상" else "이하"} ]
            현재가 : ${dto.collectedPrice}
            """.trimIndent()

        mailService.sendText(dto.requestEmail, subject, body)
    }
}
