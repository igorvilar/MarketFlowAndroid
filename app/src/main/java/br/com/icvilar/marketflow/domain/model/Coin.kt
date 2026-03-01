package br.com.icvilar.marketflow.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coin(
    val id: Int,
    val name: String,
    val symbol: String,
    val slug: String,
    @SerialName("is_active") val isActive: Int,
    val rank: Int? = null,
    @SerialName("first_historical_data") val firstHistoricalData: String? = null,
    @SerialName("last_historical_data") val lastHistoricalData: String? = null,
    val platform: Platform? = null,
    val currency: AssetCurrency? = null
)

@Serializable
data class AssetCurrency(
    val name: String,
    val symbol: String,
    @SerialName("price_usd") val priceUsd: Double? = null
)

@Serializable
data class Platform(
    val id: Int? = null,
    val name: String? = null,
    val symbol: String? = null,
    val slug: String? = null,
    @SerialName("token_address") val tokenAddress: String? = null
)

@Serializable
data class CoinResponse(
    val data: List<Coin>,
    val status: APIStatus
)
