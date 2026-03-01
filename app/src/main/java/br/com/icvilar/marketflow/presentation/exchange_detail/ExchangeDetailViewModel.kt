package br.com.icvilar.marketflow.presentation.exchange_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.icvilar.marketflow.domain.usecase.GetExchangeCoinsUseCase
import br.com.icvilar.marketflow.domain.usecase.GetExchangeDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExchangeDetailViewModel @Inject constructor(
    private val getExchangeDetailUseCase: GetExchangeDetailUseCase,
    private val getExchangeCoinsUseCase: GetExchangeCoinsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exchangeId: String = checkNotNull(savedStateHandle["exchangeId"])
    
    private val _uiState = MutableStateFlow<ExchangeDetailUiState>(ExchangeDetailUiState.Loading)
    val uiState: StateFlow<ExchangeDetailUiState> = _uiState.asStateFlow()

    init {
        fetchDetailsAndAssets()
    }

    fun fetchDetailsAndAssets() {
        viewModelScope.launch {
            _uiState.value = ExchangeDetailUiState.Loading

            val detailDeferred = async { getExchangeDetailUseCase(exchangeId) }
            val assetsDeferred = async { getExchangeCoinsUseCase(exchangeId) }

            val detailResult = detailDeferred.await()
            val assetsResult = assetsDeferred.await()

            if (detailResult.isSuccess && assetsResult.isSuccess) {
                _uiState.value = ExchangeDetailUiState.Success(
                    detail = detailResult.getOrNull()!!,
                    assets = assetsResult.getOrNull()!!
                )
            } else {
                val errorMsg = detailResult.exceptionOrNull()?.message
                    ?: assetsResult.exceptionOrNull()?.message
                    ?: "Erro desconhecido ao carregar detalhes."
                _uiState.value = ExchangeDetailUiState.Error(errorMsg)
            }
        }
    }

    fun formatCurrency(value: Double?): String {
        if (value == null) return "N/A"
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(value)
    }

    fun formatPercentage(value: Double?): String {
        if (value == null) return "0.00%"
        return String.format(Locale.getDefault(), "%.2f%%", value * 100)
    }

    fun formatDate(dateString: String?): String {
        if (dateString == null) return "Unknown Launch Date"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val date = inputFormat.parse(dateString)
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}
