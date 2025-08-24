package com.todaystock.api.common.config

import com.todaystock.api.dto.response.SuccessResponseDto
import kotlinx.coroutines.channels.Channel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChannelConfig(
        @Value("\${batch.size}")
        private val batchSize: Int,
) {
    @Bean
    fun successResponseChannel(): Channel<SuccessResponseDto> {
        return Channel(capacity = batchSize + 200)
    }
}
