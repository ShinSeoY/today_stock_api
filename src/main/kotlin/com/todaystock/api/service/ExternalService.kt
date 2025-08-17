package com.todaystock.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.todaystock.api.common.kafka.KafkaProducer
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
        private val objectMapper: ObjectMapper,
        private val mailService: MailService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publishAlarms() {
        val entries = redisRepository.findEntriesByPrefix("todaystock:", AlarmInfo::class.java)
        if (entries.isEmpty()) return

        val successKeys = mutableListOf<String>()

        entries.forEach { (key, alarm) ->
            runCatching {
                kafkaProducer.sendMessages(objectMapper.writeValueAsString(alarm))
            }.onSuccess {
                successKeys += key
            }.onFailure { e ->
                logger.error("Kafka send failed for key=$key", e)
            }
        }

        if (successKeys.isNotEmpty()) {
            val deleted = redisRepository.deleteAll(successKeys)
            logger.info("Deleted $deleted keys from Redis")
        }
    }

    @Async
    fun sendEmail(dto: EmailDto) {
        val subject = "오늘의 주식 알림"
        val body = """
            종목명 : ${dto.name} 
            설정가 : ${dto.requestPrice} [ ${ if (dto.conditionType == ConditionType.GTE) "이상" else "이하" } ]
            현재가 : ${dto.collectedPrice}
        """.trimIndent()

        mailService.sendText(dto.requestEmail, subject, body)
    }

}