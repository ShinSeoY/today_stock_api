package com.todaystock.api.controller

import com.todaystock.api.common.kafka.KafkaProducer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/external")
class ExternalController (
        private val kafkaProducer: KafkaProducer,
){
    @PostMapping("/kafka/send")
    fun sendMessage(
            @RequestBody message: String,
    ): ResponseEntity<String> {
        kafkaProducer.sendMessages(message)
        return ResponseEntity.ok("메시지 전송 완료")
    }
}