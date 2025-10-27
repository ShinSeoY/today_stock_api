package com.todaystock.api.service

import com.todaystock.api.dto.response.NaverStockSearchResponse
import com.todaystock.api.dto.response.StockPollingResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@Service
class ClientService(
        private val naverWebClient: WebClient.Builder,
) {
    fun searchStock(
            keyword: String,
            page: Int? = 1,
    ): NaverStockSearchResponse? {
        val uri =
                UriComponentsBuilder
                        .fromUriString("https://m.stock.naver.com/front-api/search")
                        .queryParam("q", keyword)
                        .queryParam("size", 20)
                        .queryParam("target", "stock,index,marketindicator,coin,ipo")
                        .queryParam("page", page)
                        .build()
                        .encode()
                        .toUri()

        return naverWebClient.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(NaverStockSearchResponse::class.java)
                .block()
    }

    fun getStockDetail(subUrl: String): StockPollingResponse? {
        val uri =
                UriComponentsBuilder
                        .fromUriString("https://polling.finance.naver.com/api/realtime${cleanUrlPath(subUrl)}")
                        .build()
                        .toUri()
        return naverWebClient.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(StockPollingResponse::class.java)
                .block()
    }

    private fun cleanUrlPath(subUrl: String): String {
        return if (subUrl.endsWith("/total")) {
            subUrl.removeSuffix("/total")
        } else {
            subUrl
        }
    }
}
