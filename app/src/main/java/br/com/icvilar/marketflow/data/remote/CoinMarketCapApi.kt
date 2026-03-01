package br.com.icvilar.marketflow.data.remote

import br.com.icvilar.marketflow.domain.model.AssetResponse
import br.com.icvilar.marketflow.domain.model.ExchangeInfoResponse
import br.com.icvilar.marketflow.domain.model.ExchangeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinMarketCapApi {

    @GET("v1/exchange/map")
    suspend fun getExchanges(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ExchangeResponse>

    @GET("v1/exchange/info")
    suspend fun getExchangeDetails(
        @Query("id") id: String
    ): Response<ExchangeInfoResponse>

    @GET("v1/exchange/assets") // Mapeamento correto de acordo com o Service do iOS
    suspend fun getExchangeAssets(
        @Query("id") id: String
    ): Response<AssetResponse>
}
