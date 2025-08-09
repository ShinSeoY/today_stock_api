package com.todaystock.api.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Service

@Service
class RedisRepository(
        private val redisTemplate: RedisTemplate<String, Any>,
        private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    // 객체 저장
    fun save(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    // 객체 조회
    fun <T> get(key: String, clazz: Class<T>): T? {
        val value = redisTemplate.opsForValue().get(key)
        return value?.let { clazz.cast(it) }
    }

    // 삭제
    fun delete(key: String): Boolean = redisTemplate.delete(key)

    // 키 존재 여부 확인
    fun exists(key: String): Boolean = redisTemplate.hasKey(key)

    // 키 확인
    fun getKeys(pattern: String): Set<String> = redisTemplate.keys(pattern)

    // prefix 로 시작하는 모든 값 조회
    fun <T : Any> findValuesByPrefix(prefix: String, clazz: Class<T>): List<T> {
        val results = mutableListOf<T>()

        redisTemplate.execute { connection ->
            // 1) 키 스캔 (raw keys)
            val rawKeys = mutableListOf<ByteArray>()
            val scanOpt = ScanOptions.scanOptions().match("$prefix*").count(500).build()
            connection.scan(scanOpt).use { cur ->
                while (cur.hasNext()) rawKeys += cur.next()
            }
            if (rawKeys.isEmpty()) return@execute null

            // 2) 값 조회 (키별 GET - raw bytes)
            val stringCmds = connection.stringCommands()
            val rawValues: List<ByteArray?> = rawKeys.map { key -> stringCmds.get(key) }

            // 3) 바이트 → POJO
            rawValues.forEach { bytes ->
                if (bytes != null) {
                    runCatching { objectMapper.readValue(bytes, clazz) }
                            .onSuccess { results += it }
                            .onFailure {
                                logger.error("Failed to deserialize one value", it)
                            }
                }
            }
            null
        }
        return results
    }


}
