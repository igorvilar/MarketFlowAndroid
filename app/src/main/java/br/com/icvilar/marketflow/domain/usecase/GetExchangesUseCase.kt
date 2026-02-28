package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangesUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(): Result<List<Exchange>> {
        return repository.getExchanges()
    }
}
