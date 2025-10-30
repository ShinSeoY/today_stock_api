package com.todaystock.api.service

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todaystock.api.common.kafka.KafkaProducer
import com.todaystock.api.common.utils.CalcUtil
import com.todaystock.api.dto.request.EmailDto
import com.todaystock.api.entity.AlarmInfo
import com.todaystock.api.entity.ConditionType
import com.todaystock.api.repository.RedisRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ExternalService(
        private val redisRepository: RedisRepository,
        private val kafkaProducer: KafkaProducer,
        private val mailService: MailService,
        private val calcUtil: CalcUtil,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publishAlarms() {
        val entries = redisRepository.findEntriesByPrefixExcludeLock("todaystock:", AlarmInfo::class.java)
        if (entries.isEmpty()) return

        val objectMapper = jacksonObjectMapper()

        entries.forEach { (key, alarm) ->
            val lockKey = "$key:lock"
            val configHash = alarm.configHash

            val curr = redisRepository.get(lockKey)
            // 동일한 값은 스킵
            if (curr == configHash) {
                return@forEach
            }

            runCatching {
                redisRepository.save(lockKey, configHash!!)
                kafkaProducer.sendMessages(objectMapper.writeValueAsString(
                        objectMapper.convertValue<Map<String, Any?>>(alarm)
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
