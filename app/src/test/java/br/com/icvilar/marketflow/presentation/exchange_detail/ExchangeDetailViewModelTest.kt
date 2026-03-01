package br.com.icvilar.marketflow.presentation.exchange_detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import br.com.icvilar.marketflow.domain.model.Asset
import br.com.icvilar.marketflow.domain.model.AssetCurrency
import br.com.icvilar.marketflow.domain.model.ExchangeDetail
import br.com.icvilar.marketflow.domain.usecase.GetExchangeCoinsUseCase
import br.com.icvilar.marketflow.domain.usecase.GetExchangeDetailUseCase
import br.com.icvilar.marketflow.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getExchangeDetailUseCase: GetExchangeDetailUseCase = mockk()
    private val getExchangeCoinsUseCase: GetExchangeCoinsUseCase = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private val exchangeId = "270"

    private fun createViewModel(): ExchangeDetailViewModel {
        every { savedStateHandle.get<String>("exchangeId") } returns exchangeId
        return ExchangeDetailViewModel(
            getExchangeDetailUseCase,
            getExchangeCoinsUseCase,
            savedStateHandle
        )
    }

    @Test
    fun `init fetches details and assets successfully and sets state to Success`() = runTest {
        // Arrange
        val detail = ExchangeDetail(id = 270, name = "Binance")
        val assets = listOf(Asset(currency = AssetCurrency(name = "Bitcoin", symbol = "BTC", priceUsd = 50000.0)))
        
        coEvery { getExchangeDetailUseCase(exchangeId) } returns Result.success(detail)
        coEvery { getExchangeCoinsUseCase(exchangeId) } returns Result.success(assets)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val finalState = awaitItem()
            assertTrue(finalState is ExchangeDetailUiState.Success)
            
            val successState = finalState as ExchangeDetailUiState.Success
            assertEquals("Binance", successState.detail.name)
            assertEquals(1, successState.assets.size)
            assertEquals("Bitcoin", successState.assets[0].currency?.name)
        }
    }

    @Test
    fun `init sets state to Error when detail fetch fails`() = runTest {
        // Arrange
        val errorMessage = "Error fetching details"
        val assets = listOf(Asset(currency = AssetCurrency(name = "Bitcoin", symbol = "BTC", priceUsd = 50000.0)))

        coEvery { getExchangeDetailUseCase(exchangeId) } returns Result.failure(Exception(errorMessage))
        coEvery { getExchangeCoinsUseCase(exchangeId) } returns Result.success(assets)

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val finalState = awaitItem()
            assertTrue(finalState is ExchangeDetailUiState.Error)
            assertEquals(errorMessage, (finalState as ExchangeDetailUiState.Error).message)
        }
    }

    @Test
    fun `init sets state to Error when assets fetch fails`() = runTest {
        // Arrange
        val detail = ExchangeDetail(id = 270, name = "Binance")
        val errorMessage = "Error fetching assets"

        coEvery { getExchangeDetailUseCase(exchangeId) } returns Result.success(detail)
        coEvery { getExchangeCoinsUseCase(exchangeId) } returns Result.failure(Exception(errorMessage))

        // Act
        val viewModel = createViewModel()

        // Assert
        viewModel.uiState.test {
            val finalState = awaitItem()
            assertTrue(finalState is ExchangeDetailUiState.Error)
            assertEquals(errorMessage, (finalState as ExchangeDetailUiState.Error).message)
        }
    }

    @Test
    fun `formatCurrency formats values correctly`() {
        // Arrange
        coEvery { getExchangeDetailUseCase(exchangeId) } returns Result.failure(Exception())
        coEvery { getExchangeCoinsUseCase(exchangeId) } returns Result.failure(Exception())
        val viewModel = createViewModel()

        // Act & Assert
        assertEquals("N/A", viewModel.formatCurrency(null))
        
        val formatted = viewModel.formatCurrency(1234.56)
        assertTrue(formatted.contains("1,234.56") || formatted.contains("1234.56")) // A moeda exibe $ localmente ou US dependentemente da configuração do locale de Runtime, mas este é locale US na source.
    }

    @Test
    fun `formatPercentage formats values correctly`() {
        // Arrange
        coEvery { getExchangeDetailUseCase(exchangeId) } returns Result.failure(Exception())
        coEvery { getExchangeCoinsUseCase(exchangeId) } returns Result.failure(Exception())
        val viewModel = createViewModel()

        // Act & Assert
        // A Locale interna do Kotlin afeta os decimais, então usamos replace() se testar no linux vs macOS
        assertEquals("0.00%", viewModel.formatPercentage(null).replace(",", "."))
        assertEquals("10.50%", viewModel.formatPercentage(0.105).replace(",", "."))
    }

    @Test
    fun `formatDate parses full ISO date correctly`() {
        // Arrange
        coEvery { getExchangeDetailUseCase(exchangeId) } returns Result.failure(Exception())
        coEvery { getExchangeCoinsUseCase(exchangeId) } returns Result.failure(Exception())
        val viewModel = createViewModel()

        // Act & Assert
        assertEquals("Unknown Launch Date", viewModel.formatDate(null))
        assertEquals("Jul 14, 2017", viewModel.formatDate("2017-07-14T00:00:00.000Z"))
        
        // Formato mal-formado retorna ele mesmo na UI no Try/Catch
        assertEquals("Invalid Date", viewModel.formatDate("Invalid Date"))
    }
}
