package com.todaystock.api.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisRepository (
    private val redisTemplate: RedisTemplate<String, Any>
) {

    // 객체 저장
    fun save(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    // 객체 조회
    fun <T> get(key: String, clazz: Class<T>): T? {
        val value = redisTemplate.opsForValue().get(key)
        return value?.let { clazz.cast(it) }
    }

    // 키 삭제
    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    // 키 존재 여부 확인
    fun exists(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    // 모든 키 조회 (패턴 기반)
    fun getKeys(pattern: String): Set<String> {
        return redisTemplate.keys(pattern)
    }
}