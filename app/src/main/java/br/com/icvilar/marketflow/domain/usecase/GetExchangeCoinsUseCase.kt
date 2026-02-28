package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.Coin
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeCoinsUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(id: String): Result<List<Coin>> {
        return repository.getExchangeCoins(id)
    }
}
