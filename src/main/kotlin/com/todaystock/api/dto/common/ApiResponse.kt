package com.todaystock.api.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
)

data class ApiResponse<T>(
    val data: T? = null,
    val error: ErrorResponse? = null,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(data = data)
        }

        fun <T> error(
            code: String,
            message: String,
        ): ApiResponse<T> {
            return ApiResponse(error = ErrorResponse(code, message))
        }
    }
}
