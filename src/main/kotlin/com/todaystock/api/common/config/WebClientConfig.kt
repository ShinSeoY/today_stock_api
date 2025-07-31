package com.todaystock.api.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun naverWebClient(): WebClient.Builder {
        return WebClient.builder()
            .defaultHeader("accept", "application/json")
            .defaultHeader("accept-language", "ko-KR,ko;q=0.9")
            .defaultHeader("access-control-max-age", "86400")
            .defaultHeader("priority", "u=1, i")
            .defaultHeader(
                "sec-ch-ua",
                "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"",
            )
            .defaultHeader("sec-ch-ua-mobile", "?0")
            .defaultHeader("sec-ch-ua-platform", "macOS")
            .defaultHeader("sec-fetch-dest", "empty")
            .defaultHeader("sec-fetch-mode", "cors")
            .defaultHeader("sec-fetch-site", "same-site")
            .defaultHeader(
                "user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36",
            )
    }
}
