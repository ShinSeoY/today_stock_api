package com.todaystock.api.dto.response

data class NaverStockSearchResponse(
        val isSuccess: Boolean? = null,
        val detailCode: String? = null,
        val message: String? = null,
        val result: Result? = null,
)

data class Result(
        val query: String,
        val totalCount: Int,
        val items: List<Item>,
//    val tabsCheck: TabsCheck? = null,
)

data class Item(
    val code: String,
    val name: String,
//    val typeCode: String? = null,
//    val typeName: String? = null,
    val url: String,
//    val reutersCode: String? = null,
//    val nationCode: String? = null,
//    val nationName: String? = null,
//    val category: String? = null,
)

// data class TabsCheck(
//    val existsStock: Boolean? = null,
//    val existsIpo: Boolean? = null,
//    val existsCoin: Boolean? = null,
//    val existsIndex: Boolean? = null,
//    val existsMarketIndicator: Boolean? = null,
// )

data class PriceComparison(
    val code: String? = null,
    val text: String? = null,
    val name: String? = null,
)

data class TradeStopType(
    val code: String? = null,
    val text: String? = null,
    val name: String? = null,
)

data class StockPollingResponse(
        val pollingInterval: Int,
        val datas: List<StockData>,
        val time: String,
)

// data class DomesticStockPollingResponse(
//    override val pollingInterval: Int,
//    override val datas: List<DomesticStockData>,
//    override val time: String
// ) : StockPollingResponse

data class StockData(
    val itemCode: String? = null,
    val stockName: String? = null,
//    val stockExchangeType: StockExchangeType? = null,
    val closePrice: String? = null,
    val reutersCode: String? = null,
//    val compareToPreviousClosePrice: String? = null,
//    val compareToPreviousPrice: PriceComparison? = null,
//    val fluctuationsRatio: String? = null,
//    val tradeStopType: TradeStopType? = null,
//    val openPrice: String? = null,
//    val highPrice: String? = null,
//    val lowPrice: String? = null,
//    val accumulatedTradingVolume: String? = null,
//    val accumulatedTradingValue: String? = null,
//    val marketStatus: String? = null,
//    val localTradedAt: String? = null,
//    val overMarketPriceInfo: OverMarketPriceInfo? = null,
//    val isinCode: String? = null,
//    val myDataCode: String? = null,
//    val stockEndUrl: String? = null,
//    val symbolCode: String? = null,
    val currencyType: CurrencyType,
)

// data class WorldStockPollingResponse(
//    override val pollingInterval: Int,
//    val datas: List<WorldStockData>,
//    override val time: String
// ) : StockPollingResponse()

// data class WorldStockData(
//    val reutersCode: String? = null,
//    val stockName: String? = null,
//    val closePrice: String? = null,
//    val compareToPreviousClosePrice: String? = null,
//    val compareToPreviousPrice: PriceComparison? = null,
//    val fluctuationsRatio: String? = null,
//    val tradeStopType: TradeStopType? = null,
//    val openPrice: String? = null,
//    val highPrice: String? = null,
//    val lowPrice: String? = null,
//    val accumulatedTradingVolume: String? = null,
//    val accumulatedTradingValue: String? = null,
//    val localTradedAt: String? = null,
//    val marketStatus: String? = null,
//    val overMarketPriceInfo: OverMarketPriceInfo? = null,
//    val currencyType: CurrencyType? = null,
//    val isinCode: String? = null,
//    val myDataCode: String? = null,
//    val stockEndUrl: String? = null,
//    val marketValueFull: String? = null,
//    val marketValueHangeul: String? = null,
//    val marketValueKrwHangeul: String? = null,
// )

// data class OverMarketPriceInfo(
//    val tradingSessionType: String? = null,
//    val overMarketStatus: String? = null,
//    val overPrice: String? = null,
//    val compareToPreviousPrice: PriceComparison? = null,
//    val compareToPreviousClosePrice: String? = null,
//    val fluctuationsRatio: String? = null,
//    val localTradedAt: String? = null,
//    val tradeStopType: TradeStopType? = null
// )
//
 data class CurrencyType(
    val code: String,
    val text: String? = null,
    val name: String? = null,
 )
//
// data class StockExchangeType(
//    val code: String? = null,
//    val zoneId: String? = null,
//    val nationType: String? = null,
//    val delayTime: Int? = null,
//    val startTime: String? = null,
//    val endTime: String? = null,
//    val closePriceSendTime: String? = null,
//    val nameKor: String? = null,
//    val nameEng: String? = null,
//    val nationCode: String? = null,
//    val nationName: String? = null,
//    val stockType: String? = null,
//    val name: String? = null,
// )
