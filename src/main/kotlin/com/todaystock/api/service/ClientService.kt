package com.todaystock.api.service

import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException
import com.todaystock.api.service.dto.DomesticStockPollingResponse
import com.todaystock.api.service.dto.NaverStockSearchResponse
import com.todaystock.api.service.dto.StockPollingResponse
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

    suspend fun getStockDetail(subUrl: String): StockPollingResponse {
        val uri = UriComponentsBuilder
            .fromUriString("https://polling.finance.naver.com/api/realtime${cleanUrlPath(subUrl)}")
            .build()
            .toUri()

        return when {
            subUrl.contains("domestic") ->
                naverWebClient.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(DomesticStockPollingResponse::class.java)
                .awaitSingle()
            subUrl.contains("worldstock") ->
                naverWebClient.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(WorldStockPollingResponse::class.java)
                    .awaitSingle()
            else -> throw InvalidClientException("url is not in domestic or woldstock")

        }
    }

    private fun cleanUrlPath(subUrl: String): String {
        return if (subUrl.endsWith("/total")) {
            subUrl.removeSuffix("/total")
        } else {
            subUrl
        }
    }


}