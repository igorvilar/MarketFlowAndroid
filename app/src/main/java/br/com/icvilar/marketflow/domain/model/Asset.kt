package br.com.icvilar.marketflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    val currency: AssetCurrency? = null
)

@Serializable
data class AssetResponse(
    val data: List<Asset>,
    val status: APIStatus
)
