package com.todaystock.api.service

import com.todaystock.api.service.dto.NaverStockSearchResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class MemberService(
        private val clientService: ClientService
) {
    suspend fun getSearchList(keyword: String, page: Int?) {
        clientService.searchStock(keyword, page)
    }
}
