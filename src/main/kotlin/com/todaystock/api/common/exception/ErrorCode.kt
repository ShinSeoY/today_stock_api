package com.todaystock.api.common.exception

enum class ErrorCode(
    val code: String,
    val message: String,
    val httpStatus: Int,
) {
    // Auth
    INVALID_TOKEN("INVALID_TOKEN", "Invalid JWT token", 401),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "Expired JWT token", 401),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "Unsupported JWT token", 401),
    EMPTY_TOKEN("EMPTY_TOKEN", "JWT token is missing", 401),

    UNAUTHORIZED("UNAUTHORIZED", "Authentication required", 401),
    FORBIDDEN("FORBIDDEN", "Access denied", 403),

    // User
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", 404),
    INVALID_CREDENTIAL("INVALID_CREDENTIAL", "Invalid email or password", 401),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already in use", 400),
}
