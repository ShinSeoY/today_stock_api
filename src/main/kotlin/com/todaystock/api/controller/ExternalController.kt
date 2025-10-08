package com.todaystock.api.controller

import com.todaystock.api.dto.common.ApiResponse
import com.todaystock.api.dto.request.EmailDto
import com.todaystock.api.service.ExternalService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/external")
class ExternalController(
        private val externalService: ExternalService,
) {
    @GetMapping("/alarms/publish")
    fun publishAlarms(): ApiResponse<String> {
        return try {
            externalService.publishAlarms()
            ApiResponse.success("ok")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    @PostMapping("/email")
    fun sendEmail(
            @RequestBody dto: EmailDto,
    ): ApiResponse<String> {
        return try {
            externalService.sendEmail(dto)
            ApiResponse.success("ok")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }
}
