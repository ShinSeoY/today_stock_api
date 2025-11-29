package com.todaystock.api.common.kafka

import com.nimbusds.jose.shaded.gson.Gson
import com.todaystock.api.dto.response.FailedResponseDto
import com.todaystock.api.dto.response.SuccessResponseDto
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaConsumer(
        private val successResponseChannel: Channel<SuccessResponseDto>,
        private val failedResponseChannel: Channel<FailedResponseDto>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val gson = Gson()

    @KafkaListener(
            topics = ["today-stock.success"],
            groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun consumeSuccess(
            @Payload message: SuccessResponseDto,
            acknowledgment: Acknowledgment,
    ) {
        runCatching {
            successResponseChannel.trySend(message).getOrThrow()

            acknowledgment.acknowledge()
        }.onFailure {
            logger.error("Kafka consume failed: ${it.message}", it)
        }
    }

    @KafkaListener(
            topics = ["today-stock.dlq"],
            groupId = "\${spring.kafka.consumer.group-id}",
    )
    fun consumeFail(
            @Payload message: FailedResponseDto,
            acknowledgment: Acknowledgment,
    ) {
        runCatching {
            failedResponseChannel.trySend(message).getOrThrow()

            acknowledgment.acknowledge()
        }.onFailure {
            logger.error("Kafka consume failed: ${it.message}", it)
        }
    }
}
