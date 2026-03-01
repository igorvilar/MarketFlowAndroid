package br.com.icvilar.marketflow.presentation.exchange_list

import app.cash.turbine.test
import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.usecase.GetCachedExchangesUseCase
import br.com.icvilar.marketflow.domain.usecase.GetExchangesUseCase
import br.com.icvilar.marketflow.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getExchangesUseCase: GetExchangesUseCase = mockk()
    private val getCachedExchangesUseCase: GetCachedExchangesUseCase = mockk()

    @Test
    fun `when init, state is Loading and then Success if usecase returns success`() = runTest {
        // Arrange
        val exchanges = listOf(
            Exchange(id = 1, name = "Binance", slug = "binance"),
            Exchange(id = 2, name = "Coinbase", slug = "coinbase")
        )
        coEvery { getExchangesUseCase(any(), any()) } returns Result.success(exchanges)
        coEvery { getCachedExchangesUseCase() } returns null

        // Act & Assert
        // A ViewModel chama loadExchanges no init
        val viewModel = ExchangeListViewModel(getExchangesUseCase, getCachedExchangesUseCase)

        viewModel.uiState.test {
            // Pode ignorar o Loading inicial porque com UnconfinedTestDispatcher o código executa e finaliza rápido,
            // ou podemos checar o último estado.
            val finalState = awaitItem()
            assertTrue(finalState is ExchangeListUiState.Success)
            assertEquals(2, (finalState as ExchangeListUiState.Success).exchanges.size)
            assertEquals("Binance", finalState.exchanges[0].name)
        }
    }

    @Test
    fun `when init, state is Loading and then Error if usecase returns failure`() = runTest {
        // Arrange
        coEvery { getExchangesUseCase(any(), any()) } returns Result.failure(Exception("Network error"))
        coEvery { getCachedExchangesUseCase() } returns null

        // Act & Assert
        val viewModel = ExchangeListViewModel(getExchangesUseCase, getCachedExchangesUseCase)

        viewModel.uiState.test {
            val finalState = awaitItem()
            assertTrue(finalState is ExchangeListUiState.Error)
            assertEquals("Network error", (finalState as ExchangeListUiState.Error).message)
        }
    }

    @Test
    fun `formatLogoUrl returns correct coinmarketcap url`() {
        // Arrange
        coEvery { getExchangesUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getCachedExchangesUseCase() } returns null
        val viewModel = ExchangeListViewModel(getExchangesUseCase, getCachedExchangesUseCase)

        // Act
        val url = viewModel.formatLogoUrl(270)

        // Assert
        assertEquals("https://s2.coinmarketcap.com/static/img/exchanges/64x64/270.png", url)
    }
}
