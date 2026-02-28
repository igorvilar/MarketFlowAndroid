package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.ExchangeDetail
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeDetailUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(id: String): Result<ExchangeDetail> {
        return repository.getExchangeDetail(id)
    }
}
