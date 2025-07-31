package com.todaystock.api.service

import com.todaystock.api.dto.DetailResponseDto
import com.todaystock.api.dto.SearchResponseDto
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val clientService: ClientService,
) {
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
