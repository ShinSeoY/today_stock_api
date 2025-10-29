package com.todaystock.api.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 객체 저장
    fun save(
        key: String,
        value: Any,
    ) {
        redisTemplate.opsForValue().set(key, value)
    }

    // 객체 조회
    fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T? {
        val value = redisTemplate.opsForValue().get(key)
        return value?.let { clazz.cast(it) }
    }

    // 삭제
    fun delete(key: String): Boolean = redisTemplate.delete(key)

    fun deleteAll(keys: Collection<String>): Long = redisTemplate.delete(keys)

    // 키 존재 여부 확인
    fun exists(key: String): Boolean = redisTemplate.hasKey(key)

    // 키 확인
    fun getKeys(pattern: String): Set<String> = redisTemplate.keys(pattern)

    // 키가 없을 때만 저장 (SETNX)
    fun setIfAbsent(
        key: String,
        value: String,
    ): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(key, value) ?: false
    }

    data class RedisEntry<T>(val key: String, val value: T)

    fun <T : Any> findEntriesByPrefix(
        prefix: String,
        clazz: Class<T>,
    ): Map<String, T> {
        val result = mutableMapOf<String, T>()
        redisTemplate.execute { conn ->
            val scan = conn.scan(ScanOptions.scanOptions().match("$prefix*").count(1000).build())
            while (scan.hasNext()) {
                val keyBytes = scan.next()
                val key = String(keyBytes)
                // 락 키는 건너뛴다
                if (key.endsWith(":lock")) continue

                val raw = conn.get(keyBytes) ?: continue
                // 값이 문자열(UUID)처럼 따옴표로만 싸인 경우도 건너뛴다(방어 코드)
                if (raw.isNotEmpty() && (raw[0].toInt() == '"'.code)) continue

                try {
                    val value = objectMapper.readValue(raw, clazz)
                    result[key] = value
                } catch (e: Exception) {
                    logger.error("Failed to deserialize value for key $key", e)
                }
            }
            null
        }
        return result
    }
}
