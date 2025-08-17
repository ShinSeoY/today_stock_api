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

    fun deleteAll(keys: Collection<String>): Long =
            redisTemplate.delete(keys)

    // 키 존재 여부 확인
    fun exists(key: String): Boolean = redisTemplate.hasKey(key)

    // 키 확인
    fun getKeys(pattern: String): Set<String> = redisTemplate.keys(pattern)

    data class RedisEntry<T>(val key: String, val value: T)

    fun <T : Any> findEntriesByPrefix(prefix: String, clazz: Class<T>): List<RedisEntry<T>> {
        val results = mutableListOf<RedisEntry<T>>()

        redisTemplate.execute { connection ->
            val scanOpt = ScanOptions.scanOptions().match("$prefix*").count(500).build()
            connection.scan(scanOpt).use { cur ->
                val stringCmds = connection.stringCommands()
                while (cur.hasNext()) {
                    val rawKey = cur.next()
                    val rawVal = stringCmds.get(rawKey)
                    if (rawVal != null) {
                        runCatching {
                            val v = objectMapper.readValue(rawVal, clazz)
                            val k = String(rawKey, Charsets.UTF_8)
                            results += RedisEntry(k, v)
                        }.onFailure {
                            logger.error("Failed to deserialize value for key", it)
                        }
                    }
                }
            }
            null
        }
        return results
    }


}
