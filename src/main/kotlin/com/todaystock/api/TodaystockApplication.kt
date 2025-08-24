package com.todaystock.api

import com.todaystock.api.dto.response.SuccessResponseDto
import com.todaystock.api.service.BufferService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CommandLineRunnerSample(
        private val bufferService: BufferService,
        private val successResponseChannel: Channel<SuccessResponseDto>,
        @Value("\${batch.size}")
        private val batchSize: Int,
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(CommandLineRunner::class.java)
    private val buffer = mutableListOf<SuccessResponseDto>()

    override fun run(args: Array<String>): Unit =
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    while (isActive) {
                        val successResponse = successResponseChannel.receive()
                        buffer.add(successResponse)

                        if (buffer.size >= batchSize) {
                            bufferService.flushBuffer(buffer, batchSize)
                        }
                    }
                }
            }

    @Scheduled(fixedRate = 1 * 60 * 1000L)
    fun flushBufferScheduler() {
        logger.info("Starting flush buffer.")
        if (buffer.isNotEmpty()) {
            bufferService.flushBuffer(buffer, batchSize)
        }
    }
}

@EnableScheduling
@SpringBootApplication
class TodaystockApplication

fun main(args: Array<String>) {
    runApplication<TodaystockApplication>(*args)
}