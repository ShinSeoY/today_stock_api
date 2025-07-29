package com.todaystock.api.service

import com.todaystock.api.service.dto.DomesticStockPollingResponse
import com.todaystock.api.service.dto.NaverStockSearchResponse
import com.todaystock.api.service.dto.WorldStockPollingResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@Service
class ClientService (
    private val naverWebClient: WebClient.Builder
){

    suspend fun searchStock(keyword: String, page: Int? = 1): NaverStockSearchResponse {
        val uri = UriComponentsBuilder
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
            .awaitSingle()
    }

    suspend fun getDomesticStockDetail(code: String): DomesticStockPollingResponse {
        val uri = UriComponentsBuilder
            .fromUriString("https://polling.finance.naver.com/api/realtime/domestic/stock/${code}")
            .build()
            .toUri()

        return naverWebClient.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(DomesticStockPollingResponse::class.java)
            .awaitSingle()
    }

    suspend fun getWorldStockDetail(code: String): WorldStockPollingResponse {
        val uri = UriComponentsBuilder
            .fromUriString("https://polling.finance.naver.com/api/realtime/worldstock/stock/${code}")
            .build()
            .toUri()

        return naverWebClient.build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono(WorldStockPollingResponse::class.java)
            .awaitSingle()
    }

}