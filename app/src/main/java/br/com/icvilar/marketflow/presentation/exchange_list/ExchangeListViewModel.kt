package br.com.icvilar.marketflow.presentation.exchange_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.usecase.GetCachedExchangesUseCase
import br.com.icvilar.marketflow.domain.usecase.GetExchangesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeListViewModel @Inject constructor(
    private val getExchangesUseCase: GetExchangesUseCase,
    private val getCachedExchangesUseCase: GetCachedExchangesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExchangeListUiState>(ExchangeListUiState.Loading)
    val uiState: StateFlow<ExchangeListUiState> = _uiState.asStateFlow()

    private var currentStart = 1
    private val limitPerPage = 50
    private var hasMoreData = true
    private var isFetchingMore = false
    private val currentExchanges = mutableListOf<Exchange>()

    init {
        loadExchanges()
    }

    fun loadExchanges() {
        viewModelScope.launch {
            val cached = getCachedExchangesUseCase()
            if (!cached.isNullOrEmpty()) {
                currentExchanges.clear()
                currentExchanges.addAll(cached)
                _uiState.value = ExchangeListUiState.Success(currentExchanges.toList(), false)
            } else {
                _uiState.value = ExchangeListUiState.Loading
            }

            currentStart = 1
            hasMoreData = true

            getExchangesUseCase(start = currentStart, limit = limitPerPage)
                .onSuccess { exchanges ->
                    hasMoreData = exchanges.size == limitPerPage
                    currentExchanges.clear()
                    currentExchanges.addAll(exchanges)
                    _uiState.value = ExchangeListUiState.Success(currentExchanges.toList(), false)
                }
                .onFailure { error ->
                    if (currentExchanges.isEmpty()) {
                        _uiState.value = ExchangeListUiState.Error(
                            error.message ?: "Erro desconhecido ao carregar exchanges"
                        )
                    }
                }
        }
    }

    fun fetchMoreExchanges() {
        if (isFetchingMore || !hasMoreData) return
        val currentState = _uiState.value
        if (currentState is ExchangeListUiState.Success) {
            isFetchingMore = true
            _uiState.value = currentState.copy(isFetchingMore = true)

            viewModelScope.launch {
                currentStart += limitPerPage
                getExchangesUseCase(start = currentStart, limit = limitPerPage)
                    .onSuccess { newBatch ->
                        hasMoreData = newBatch.size == limitPerPage
                        currentExchanges.addAll(newBatch)
                        _uiState.value = ExchangeListUiState.Success(currentExchanges.toList(), false)
                        isFetchingMore = false
                    }
                    .onFailure {
                        // Volta o indice em caso de erro no fetch more e esconde o loading
                        currentStart -= limitPerPage
                        _uiState.value = currentState.copy(isFetchingMore = false)
                        isFetchingMore = false
                    }
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
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val outputFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val date = inputFormat.parse(dateString)
            if (date != null) "Launched: ${outputFormat.format(date)}" else "Launched: ${dateString.take(10)}"
        } catch (e: Exception) {
            "Launched: ${dateString.take(10)}"
        }
    }
}
