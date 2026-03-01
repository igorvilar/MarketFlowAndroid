package br.com.icvilar.marketflow.domain.repository

import br.com.icvilar.marketflow.domain.model.Coin
import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.model.ExchangeDetail

interface ExchangeRepository {
    suspend fun getExchanges(start: Int = 1, limit: Int = 50): Result<List<Exchange>>
    suspend fun loadCachedExchanges(): List<Exchange>?
    suspend fun saveExchangesToCache(exchanges: List<Exchange>)
    suspend fun getExchangeDetail(id: String): Result<ExchangeDetail>
    suspend fun getExchangeCoins(id: String): Result<List<Coin>>
}
