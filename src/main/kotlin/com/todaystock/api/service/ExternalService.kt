package com.todaystock.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.todaystock.api.common.kafka.KafkaProducer
import com.todaystock.api.entity.AlarmInfo
import com.todaystock.api.repository.RedisRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ExternalService(
        private val redisRepository: RedisRepository,
        private val kafkaProducer: KafkaProducer,
        private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publishAlarms() {
        val res = getAllTodayStockAlarms()
        res.forEach {
            kafkaProducer.sendMessages(objectMapper.writeValueAsString(it))
        }
    }

    private fun getAllTodayStockAlarms(): List<AlarmInfo> =
            redisRepository.findValuesByPrefix("todaystock:", AlarmInfo::class.java)
}