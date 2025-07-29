package com.todaystock.api.todaystock

import com.todaystock.api.service.ClientService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TodaystockApplicationTests (
    @Autowired
    private val clientService: ClientService){

    @Test
    fun test() {
        runBlocking {
            val res = clientService.getDomesticStockDetail("005930")
//           val res = clientService.getWorldStockDetail("NVDA.O")
            println(res.toString())
        }
    }
}
