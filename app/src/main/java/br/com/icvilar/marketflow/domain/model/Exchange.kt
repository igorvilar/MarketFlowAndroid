package br.com.icvilar.marketflow.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exchange(
    val id: Int,
    val name: String,
    val slug: String,
    @SerialName("first_historical_data") val firstHistoricalData: String? = null,
    val logoUrl: String? = "https://s2.coinmarketcap.com/static/img/exchanges/64x64/\$id.png" // Converted from swift computed property map
)

@Serializable
data class ExchangeResponse(
    val data: List<Exchange>,
    val status: APIStatus
)
