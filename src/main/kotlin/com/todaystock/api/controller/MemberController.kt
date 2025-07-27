package com.todaystock.api.controller

import com.todaystock.api.common.kafka.KafkaProducer
import com.todaystock.api.entity.Member
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class MemberController(
    private val kafkaProducer: KafkaProducer,
) {
    @GetMapping("/alim")
    fun getAlimList(
        @CurrentUser member: Member,
    ): ResponseEntity<String> {
        println("요청한 사용자 이메일: ${member.email}")
        return ResponseEntity.ok("메시지 전송 완료")
    }
}
