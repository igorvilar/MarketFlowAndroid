package br.com.icvilar.marketflow.presentation.exchange_list

import br.com.icvilar.marketflow.domain.model.Exchange

sealed class ExchangeListUiState {
    data object Loading : ExchangeListUiState()
    data class Success(val exchanges: List<Exchange>) : ExchangeListUiState()
    data class Error(val message: String) : ExchangeListUiState()
}
