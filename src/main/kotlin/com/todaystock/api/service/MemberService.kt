package com.todaystock.api.service

import com.todaystock.api.dto.response.DetailResponseDto
import com.todaystock.api.dto.response.SearchResponseDto
import com.todaystock.api.dto.request.AlimRequestDto
import com.todaystock.api.dto.request.SearchRequestDto
import com.todaystock.api.dto.response.AlarmResponseDto
import com.todaystock.api.entity.*
import com.todaystock.api.repository.AlarmRepository
import com.todaystock.api.repository.RedisRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        val calcPrice = dto.requestPrice ?: (dto.currentPrice * (1 +  dto.percent!!/100))
        alarmRepository.save(
            Alarm(
                alarmId = AlarmId(
                    memberEmail = memberEmail,
                    memberProvider = memberProvider,
                    code = code
                ),
                name = dto.stock.name,
                currencyCode = dto.stock.currencyCode,
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

    fun getAlarms(member: Member): List<AlarmResponseDto>{
        val res = alarmRepository.findAllByMember_MemberId_EmailAndMember_MemberId_Provider(member.memberId.email, member.memberId.provider)
        return res.map {
            AlarmResponseDto(
                    code = it.alarmId.code,
                    name = it.name,
                    price = it.price,
                    condition = it.conditionType,
                    email = it.email,
                    date = it.createdAt,
                    currencyCode = it.currencyCode
            )
        }
    }

    fun removeAlarm(member: Member, code: String) {
        alarmRepository.deleteById(AlarmId(
                memberEmail = member.memberId.email,
                memberProvider = member.memberId.provider,
                code = code
        ))
    }

    suspend fun getSearchList(
        dto: SearchRequestDto
    ): List<SearchResponseDto> {
        val res = clientService.searchStock(dto.keyword!!, dto.page)
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
                    price = clearPrice(detail.closePrice),
                    currencyCode = detail.currencyType.code
                )
            } else {
                DetailResponseDto(
                    code = detail.reutersCode!!,
                    name = detail.stockName!!,
                    price = clearPrice(detail.closePrice),
                    currencyCode = detail.currencyType.code
                )
            }
        } else {
            null
        }
    }

    fun clearPrice(price: String?): Double {
        return try {
            price?.replace(",", "")
                    ?.trim()
                    ?.toDouble() ?: 0.0
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}
