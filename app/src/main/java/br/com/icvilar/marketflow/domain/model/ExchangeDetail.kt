package br.com.icvilar.marketflow.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeDetail(
    val id: Int,
    val name: String,
    val logo: String? = null,
    val description: String? = null,
    @SerialName("maker_fee") val makerFee: Double? = null,
    @SerialName("taker_fee") val takerFee: Double? = null,
    @SerialName("date_launched") val dateLaunched: String? = null,
    val urls: ExchangeURLs? = null
)

@Serializable
data class ExchangeURLs(
    val website: List<String>? = null,
    val fee: List<String>? = null,
    val twitter: List<String>? = null
)

@Serializable
data class ExchangeInfoResponse(
    val data: Map<String, ExchangeDetail>,
    val status: APIStatus
)
