package br.com.icvilar.marketflow.presentation.exchange_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.icvilar.marketflow.domain.usecase.GetExchangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeListViewModel @Inject constructor(
    private val getExchangesUseCase: GetExchangesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExchangeListUiState>(ExchangeListUiState.Loading)
    val uiState: StateFlow<ExchangeListUiState> = _uiState.asStateFlow()

    init {
        loadExchanges()
    }

    fun loadExchanges() {
        viewModelScope.launch {
            _uiState.value = ExchangeListUiState.Loading

            getExchangesUseCase()
                .onSuccess { exchanges ->
                    _uiState.value = ExchangeListUiState.Success(exchanges)
                }
                .onFailure { error ->
                    _uiState.value = ExchangeListUiState.Error(
                        error.message ?: "Erro desconhecido ao carregar exchanges"
                    )
                }
        }
    }

    fun formatLogoUrl(exchangeId: Int): String {
        return "https://s2.coinmarketcap.com/static/img/exchanges/64x64/$exchangeId.png"
    }

    fun formatVolume(volume: Double?): String {
        if (volume == null) return "Vol: N/A"
        return "Vol: $volume"
    }

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Launched: Unknown"
        // Formato original ISO: 2013-04-28T00:00:00.000Z
        return "Launched: ${dateString.take(10)}"
    }
}
