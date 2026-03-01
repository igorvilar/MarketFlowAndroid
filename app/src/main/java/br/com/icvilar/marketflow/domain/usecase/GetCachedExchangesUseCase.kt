package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetCachedExchangesUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(): List<Exchange>? {
        return repository.loadCachedExchanges()
    }
}
