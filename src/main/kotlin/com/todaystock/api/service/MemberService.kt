package com.todaystock.api.service

import com.todaystock.api.dto.DetailResponseDto
import com.todaystock.api.dto.SearchResponseDto
import com.todaystock.api.dto.request.AlimRequestDto
import com.todaystock.api.entity.*
import com.todaystock.api.repository.AlarmRepository
import com.todaystock.api.repository.RedisRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val clientService: ClientService,
    private val alarmRepository: AlarmRepository,
    private val redisRepository: RedisRepository,
) {
    fun saveAlarm(member: Member, dto: AlimRequestDto){
        val memberEmail = member.memberId.email
        val memberProvider = member.memberId.provider
        val code = dto.stock.code

        val calcPrice = dto.requestPrice ?: (dto.currentPrice * dto.percent!! * 0.01)
        alarmRepository.save(
            Alarm(
                alarmId = AlarmId(
                    memberEmail = memberEmail,
                    memberProvider = memberProvider,
                    code = code
                ),
                email = dto.requestEmail,
                price = calcPrice,
                conditionType = ConditionType.valueOf(dto.condition),
                url = dto.stock.url,
                enable = true,
                member = member
            )
        )

        val key = "todaystock:${memberEmail}:${memberProvider}:${code}"
        val value = AlarmInfo(
            memberProvider = memberProvider.name,
            memberEmail = memberEmail,
            requestEmail = dto.requestEmail,
            requestUrl = dto.stock.url,
            requestPrice = calcPrice.toString()
        )
        redisRepository.save(key, value)
    }

    suspend fun getSearchList(
        keyword: String,
        page: Int?,
    ): List<SearchResponseDto> {
        val res = clientService.searchStock(keyword, page)
        return res?.result?.items?.map {
            SearchResponseDto(
                code = it.code,
                name = it.name,
                url = it.url,
            )
        } ?: emptyList()
    }

    suspend fun getStockDetail(url: String): DetailResponseDto? {
        val res = clientService.getStockDetail(url)
        return if (res.datas.isNotEmpty()) {
            val detail = res.datas[0]

            if (url.contains("domestic")) {
                DetailResponseDto(
                    code = detail.itemCode!!,
                    name = detail.stockName!!,
                    price = detail.closePrice?.toDouble() ?: 0.0,
                )
            } else {
                DetailResponseDto(
                    code = detail.reutersCode!!,
                    name = detail.stockName!!,
                    price = detail.closePrice?.toDouble() ?: 0.0,
                )
            }
        } else {
            null
        }
    }
}
