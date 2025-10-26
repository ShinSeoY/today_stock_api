package com.todaystock.api.service

import com.todaystock.api.dto.request.AlimRequestDto
import com.todaystock.api.dto.request.SearchRequestDto
import com.todaystock.api.dto.response.AlarmResponseDto
import com.todaystock.api.dto.response.DetailResponseDto
import com.todaystock.api.dto.response.SearchResponseDto
import com.todaystock.api.entity.*
import com.todaystock.api.repository.AlarmRepository
import com.todaystock.api.repository.RedisRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemberService(
        private val clientService: ClientService,
        private val alarmRepository: AlarmRepository,
        private val redisRepository: RedisRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun saveAlarm(
            member: Member,
            dto: AlimRequestDto,
    ) {
        val memberEmail = member.memberId.email
        val memberProvider = member.memberId.provider
        val code = dto.stock.code

        val calcPrice = dto.calcPrice ?: dto.requestPrice ?: (dto.currentPrice * (1 + dto.percent!! / 100))
        alarmRepository.save(
                Alarm(
                        alarmId =
                        AlarmId(
                                memberEmail = memberEmail,
                                memberProvider = memberProvider,
                                code = code,
                        ),
                        name = dto.stock.name,
                        currencyCode = dto.stock.currencyCode,
                        email = dto.requestEmail,
                        price = calcPrice,
                        conditionType = ConditionType.valueOf(dto.condition),
                        url = dto.stock.url,
                        enable = true,
                        member = member,
                ),
        )

        val key = "todaystock:$memberEmail:$memberProvider:$code"
        val value =
                AlarmInfo(
                        memberProvider = memberProvider.name,
                        memberEmail = memberEmail,
                        name = dto.stock.name,
                        conditionType = ConditionType.valueOf(dto.condition),
                        requestEmail = dto.requestEmail,
                        requestUrl = dto.stock.url,
                        requestPrice = calcPrice.toString(),
                        code = dto.stock.code,
                )
        redisRepository.save(key, value)
    }

    fun getAlarms(member: Member): List<AlarmResponseDto> {
        val res = alarmRepository.findAllByMember_MemberId_EmailAndMember_MemberId_Provider(member.memberId.email, member.memberId.provider)
        return res.map {
            AlarmResponseDto(
                    code = it.alarmId.code,
                    url = it.url,
                    name = it.name,
                    price = it.price,
                    condition = it.conditionType,
                    email = it.email,
                    date = it.createdAt,
                    currencyCode = it.currencyCode,
                    enable = it.enable
            )
        }
    }

    fun removeAlarm(
            member: Member,
            code: String,
    ) {
        alarmRepository.deleteById(
                AlarmId(
                        memberEmail = member.memberId.email,
                        memberProvider = member.memberId.provider,
                        code = code,
                ),
        )
        val key = "todaystock:${member.memberId.email}:${member.memberId.provider}:$code"
        redisRepository.delete(key)
    }

    fun disableAlarm(
            member: Member,
            code: String,
    ) {
        alarmRepository.bulkUpdateEnableByIds(
                enable = false,
                listOf(AlarmId(
                        memberEmail = member.memberId.email,
                        memberProvider = member.memberId.provider,
                        code = code,
                )),
        )
        val key = "todaystock:${member.memberId.email}:${member.memberId.provider}:$code"
        redisRepository.delete(key)
    }

    fun bulkUpdateAlarmStatus(alarmIds: List<AlarmId>) {
        if (alarmIds.isEmpty()) return

        val keys = alarmIds.map { "todaystock:${it.memberEmail}:${it.memberProvider}:${it.code}" }

        val successCnt = alarmRepository.bulkUpdateEnableByIds(false, alarmIds)
        logger.info("updated $successCnt alarms status")

        if (successCnt == keys.size) {
            val lockKeys = keys.map { "$it:lock" }
            // 원본 키 + 락 키 동시 삭제
            redisRepository.deleteAll(keys + lockKeys)
            logger.info("Deleted ${keys.size} keys and ${lockKeys.size} locks from Redis")
        } else {
            logger.error("❗️조회된 키 수와 업데이트 성공 수가 다릅니다. 일부만 삭제를 건너뜁니다.")
            // 부분 성공 시: 실제로 업데이트 성공한 ID만 추려서 해당 키/락만 지우는 로직을 권장
            // (ex. repo가 성공한 알람ID를 반환하도록 하거나, 개별 업데이트로 전환)
        }
    }

    suspend fun getSearchList(dto: SearchRequestDto): List<SearchResponseDto> {
        val res = clientService.searchStock(dto.keyword!!, dto.page)

        return res?.result?.items
                ?.filter {
                    it.url.startsWith("/worldstock", ignoreCase = true) ||
                            it.url.startsWith("/domestic", ignoreCase = true)
                }?.map {
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
                        currencyCode = detail.currencyType.code,
                )
            } else {
                DetailResponseDto(
                        code = detail.reutersCode!!,
                        name = detail.stockName!!,
                        price = clearPrice(detail.closePrice),
                        currencyCode = detail.currencyType.code,
                )
            }
        } else {
            null
        }
    }

    private fun clearPrice(price: String?): Double {
        return try {
            price?.replace(",", "")
                    ?.trim()
                    ?.toDouble() ?: 0.0
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}
