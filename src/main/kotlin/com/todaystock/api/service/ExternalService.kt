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
import java.time.Duration
import java.util.*

@Service
class ExternalService(
    private val redisRepository: RedisRepository,
    private val kafkaProducer: KafkaProducer,
    private val objectMapper: ObjectMapper,
    private val mailService: MailService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publishAlarms() {
        val entries = redisRepository.findEntriesByPrefix("todaystock:", AlarmInfo::class.java)
        if (entries.isEmpty()) return

        entries.forEach { (key, alarm) ->
            println("----/v1/external/alarms/publish 2222")
            val lockKey = "$key:lock"
            val token = UUID.randomUUID().toString()
            val acquired = redisRepository.setIfAbsent(lockKey, token, Duration.ofSeconds(300)) //  5분 TTL
            // 이미 처리 중(IN_FLIGHT) → 이번 사이클 건너뜀
            if (!acquired) {
                return@forEach
            }

            runCatching {
//                // 메시지에 redisKey/lockToken을 함께 넣어 내려보내면 이후 단계에서 검증/삭제에 사용 가능
//                val envelope = mapOf(
//                        "redisKey" to key,
//                        "lockToken" to token,
//                        "payload" to alarm
//                )
                kafkaProducer.sendMessages(objectMapper.writeValueAsString(alarm))
            }.onFailure { e ->
                // 발행 실패 시 락 해제 → 다음 사이클에서 재시도
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
