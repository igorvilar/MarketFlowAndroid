package br.com.icvilar.marketflow.data.repository

import android.content.Context
import android.content.SharedPreferences
import br.com.icvilar.marketflow.data.remote.CoinMarketCapApi
import br.com.icvilar.marketflow.domain.model.Asset
import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.model.ExchangeDetail
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ExchangeRepositoryImpl @Inject constructor(
    private val api: CoinMarketCapApi,
    @ApplicationContext private val context: Context
) : ExchangeRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("marketflow_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun loadCachedExchanges(): List<Exchange>? {
        val cached = prefs.getString("cached_exchanges", null)
        return if (cached != null) {
            try {
                json.decodeFromString<List<Exchange>>(cached)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    override suspend fun saveExchangesToCache(exchanges: List<Exchange>) {
        if (exchanges.isNotEmpty()) {
            prefs.edit().putString("cached_exchanges", json.encodeToString(exchanges)).apply()
        }
    }

    override suspend fun getExchanges(start: Int, limit: Int): Result<List<Exchange>> {
        return try {
            val response = api.getExchanges(start = start, limit = limit)
            if (response.isSuccessful && response.body() != null) {
                // Checa o status interno do json (ex: erro mapeado pela API)
                val status = response.body()?.status
                if (status?.errorCode != 0 && status?.errorCode != null) {
                    Result.failure(Exception(status.errorMessage ?: "API Error: ${status.errorCode}"))
                } else {
                    val exchanges = response.body()!!.data
                    if (start == 1 && exchanges.isNotEmpty()) {
                        saveExchangesToCache(exchanges)
                    }
                    Result.success(exchanges)
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

    override suspend fun getExchangeCoins(id: String): Result<List<Asset>> {
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
