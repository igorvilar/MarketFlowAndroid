package br.com.icvilar.marketflow.presentation.exchange_detail

import br.com.icvilar.marketflow.domain.model.Asset
import br.com.icvilar.marketflow.domain.model.ExchangeDetail

sealed class ExchangeDetailUiState {
    data object Loading : ExchangeDetailUiState()
    data class Success(
        val detail: ExchangeDetail,
        val assets: List<Asset>
    ) : ExchangeDetailUiState()
    data class Error(val message: String) : ExchangeDetailUiState()
}
