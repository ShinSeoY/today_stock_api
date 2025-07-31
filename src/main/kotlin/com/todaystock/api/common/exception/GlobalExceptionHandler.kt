package com.todaystock.api.common.exception

import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<String> {
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.httpStatus).body("접근이 거부되었습니다: ${ex.message}")
    }
}
