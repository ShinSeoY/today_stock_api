package com.todaystock.api.service.dto

data class NaverStockSearchResponse(
    val isSuccess: Boolean,
    val detailCode: String,
    val message: String,
    val result: Result
)

data class Result(
    val query: String,
    val totalCount: Int,
    val items: List<Item>,
    val tabsCheck: TabsCheck
)

data class Item(
    val code: String,
    val name: String,
    val typeCode: String,
    val typeName: String,
    val url: String,
    val reutersCode: String,
    val nationCode: String,
    val nationName: String,
    val category: String
)

data class TabsCheck(
    val existsStock: Boolean,
    val existsIpo: Boolean,
    val existsCoin: Boolean,
    val existsIndex: Boolean,
    val existsMarketIndicator: Boolean
)


data class PriceComparison(
    val code: String,
    val text: String,
    val name: String
)

data class TradeStopType(
    val code: String,
    val text: String,
    val name: String
)

data class DomesticStockPollingResponse(
    val pollingInterval: Int,
    val datas: List<DomesticStockData>,
    val time: String
)

data class DomesticStockData(
    val itemCode: String,
    val stockName: String,
    val stockExchangeType: StockExchangeType,
    val closePrice: String,
    val compareToPreviousClosePrice: String,
    val compareToPreviousPrice: PriceComparison,
    val fluctuationsRatio: String,
    val tradeStopType: TradeStopType,
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val accumulatedTradingVolume: String,
    val accumulatedTradingValue: String,
    val marketStatus: String,
    val localTradedAt: String,
    val overMarketPriceInfo: OverMarketPriceInfo,
    val isinCode: String,
    val myDataCode: String?,
    val stockEndUrl: String?,
    val symbolCode: String,
    val currencyType: CurrencyType
)

data class WorldStockPollingResponse(
    val pollingInterval: Int,
    val datas: List<WorldStockData>,
    val time: String
)

data class WorldStockData(
    val reutersCode: String,
    val stockName: String,
    val symbolCode: String,
    val stockExchangeType: StockExchangeType,
    val closePrice: String,
    val compareToPreviousClosePrice: String,
    val compareToPreviousPrice: PriceComparison,
    val fluctuationsRatio: String,
    val tradeStopType: TradeStopType,
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val accumulatedTradingVolume: String,
    val accumulatedTradingValue: String,
    val localTradedAt: String,
    val marketStatus: String,
    val overMarketPriceInfo: OverMarketPriceInfo,
    val currencyType: CurrencyType,
    val isinCode: String,
    val myDataCode: String?,
    val stockEndUrl: String?,
    val marketValueFull: String,
    val marketValueHangeul: String,
    val marketValueKrwHangeul: String
)

data class OverMarketPriceInfo(
    val tradingSessionType: String,
    val overMarketStatus: String,
    val overPrice: String,
    val compareToPreviousPrice: PriceComparison,
    val compareToPreviousClosePrice: String,
    val fluctuationsRatio: String,
    val localTradedAt: String,
    val tradeStopType: TradeStopType? = null // 국내는 필수, 해외는 optional
)

data class CurrencyType(
    val code: String,
    val text: String,
    val name: String
)

data class StockExchangeType(
    val code: String,
    val zoneId: String,
    val nationType: String,
    val delayTime: Int,
    val startTime: String,
    val endTime: String,
    val closePriceSendTime: String,
    val nameKor: String,
    val nameEng: String,
    val nationCode: String,
    val nationName: String,
    val stockType: String,
    val name: String
)
