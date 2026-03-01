package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.Asset
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeCoinsUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(id: String): Result<List<Asset>> {
        return repository.getExchangeCoins(id)
    }
}
