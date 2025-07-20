package com.todaystock.api.common.kafka

import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.logging.Logger

@Service
class KafkaProducer(
        private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val TOPIC = "today-stock"
    fun sendMessages(message: String) {
            kafkaTemplate.send(TOPIC, message)
                    .whenComplete { result: SendResult<String, String>, ex: Throwable? ->
                        if (ex == null) {
                            handleSuccess(result, message)
                        } else {
                            handleFailure(ex, message)
                        }
                    }
    }


    private fun handleSuccess(result: SendResult<String, String>, message: String) {
        logger.info("Message sent successfully: {}", message)
        logger.debug("Partition: {}, Offset: {}",
                result.recordMetadata.partition(),
                result.recordMetadata.offset())
    }

    private fun handleFailure(ex: Throwable, message: String) {
        logger.error("Failed to send message: {}", message, ex)
    }
}