package com.todaystock.api.common.kafka

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
            topics = ["today-stock.success"],
            groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun consumeSuccess(msg: String, ack: Acknowledgment) {

        // todo : 100개씩 받아서 배치처리 & 1분에 한번씩 찌꺼기 데이터 커밋하도록
        logger.info("----")
        logger.info(msg)
//        ack.acknowledge()
    }
}
