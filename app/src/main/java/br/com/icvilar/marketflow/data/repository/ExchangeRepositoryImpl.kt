package br.com.icvilar.marketflow.data.repository

import br.com.icvilar.marketflow.data.remote.CoinMarketCapApi
import br.com.icvilar.marketflow.domain.model.Coin
import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.model.ExchangeDetail
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi
) : ExchangeRepository {

    override suspend fun getExchanges(): Result<List<Exchange>> {
        return try {
            val response = api.getExchanges()
            if (response.isSuccessful && response.body() != null) {
                // Checa o status interno do json (ex: erro mapeado pela API)
                val status = response.body()?.status
                if (status?.errorCode != 0 && status?.errorCode != null) {
                    Result.failure(Exception(status.errorMessage ?: "API Error: ${status.errorCode}"))
                } else {
                    Result.success(response.body()!!.data)
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExchangeDetail(id: String): Result<ExchangeDetail> {
        return try {
            val response = api.getExchangeDetails(id)
            if (response.isSuccessful && response.body() != null) {
                val status = response.body()?.status
                if (status?.errorCode != 0 && status?.errorCode != null) {
                    Result.failure(Exception(status.errorMessage ?: "API Error: ${status.errorCode}"))
                } else {
                    val detail = response.body()!!.data[id]
                    if (detail != null) {
                        Result.success(detail)
                    } else {
                        Result.failure(Exception("Exchange details not found in payload."))
                    }
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExchangeCoins(id: String): Result<List<Coin>> {
        return try {
            val response = api.getExchangeAssets(id)
            if (response.isSuccessful && response.body() != null) {
                val status = response.body()?.status
                if (status?.errorCode != 0 && status?.errorCode != null) {
                    Result.failure(Exception(status.errorMessage ?: "API Error: ${status.errorCode}"))
                } else {
                    Result.success(response.body()!!.data)
                }
            } else {
                Result.failure(Exception("HTTP Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
