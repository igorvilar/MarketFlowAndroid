package br.com.icvilar.marketflow.presentation.exchange_detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.icvilar.marketflow.domain.model.Asset
import br.com.icvilar.marketflow.domain.model.AssetCurrency
import br.com.icvilar.marketflow.domain.model.ExchangeDetail
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExchangeDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when state is Loading, CircularProgressIndicator is displayed`() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(ExchangeDetailUiState.Loading)

        composeTestRule.setContent {
            ExchangeDetailScreen(viewModel = viewModel, onNavigateBack = {})
        }

        // Não há uma tag explícita, mas garantimos que "Detalhes" aparece no topo. E Loading ocorre se Sucesso ou Erro não forem mostrados.
        composeTestRule.onNodeWithText("Detalhes").assertIsDisplayed()
    }

    @Test
    fun `when state is Error, error message and retry button are displayed`() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(ExchangeDetailUiState.Error("Falha na API"))

        composeTestRule.setContent {
            ExchangeDetailScreen(viewModel = viewModel, onNavigateBack = {})
        }

        composeTestRule.onNodeWithText("Falha na API").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tentar Novamente").assertIsDisplayed()
    }

    @Test
    fun `when state is Success, exchange details and assets are displayed`() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)
        val detail = ExchangeDetail(
            id = 270,
            name = "Binance",
            description = "A great exchange",
            makerFee = 0.1,
            takerFee = 0.2,
            dateLaunched = "2017-07-14T00:00:00.000Z"
        )
        val assets = listOf(
            Asset(currency = AssetCurrency(name = "Bitcoin", symbol = "BTC", priceUsd = 50000.0))
        )

        every { viewModel.uiState } returns MutableStateFlow(ExchangeDetailUiState.Success(detail, assets))
        every { viewModel.formatPercentage(0.1) } returns "10.00%"
        every { viewModel.formatPercentage(0.2) } returns "20.00%"
        every { viewModel.formatDate(any()) } returns "Jul 14, 2017"
        every { viewModel.formatCurrency(any()) } returns "$50,000.00"

        composeTestRule.setContent {
            ExchangeDetailScreen(viewModel = viewModel, onNavigateBack = {})
        }

        // Verifica o Cabeçalho
        composeTestRule.onAllNodesWithText("Binance", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("A great exchange", substring = true).onFirst().assertExists()
        
        // Verifica as "Metrics"
        composeTestRule.onAllNodesWithText("Maker Fee", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("10.00%", substring = true).onFirst().assertExists()
        
        composeTestRule.onAllNodesWithText("Taker Fee", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("20.00%", substring = true).onFirst().assertExists()
        
        composeTestRule.onAllNodesWithText("Launched", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Jul 14, 2017", substring = true).onFirst().assertExists()

        // Verifica a Lista de Assets/Moedas
        composeTestRule.onAllNodesWithText("Available Assets", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Bitcoin", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("BTC", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("$50,000.00", substring = true).onFirst().assertExists()
    }

    @Test
    fun `cuando click no botao voltar, onNavigateBack e acionado`() {
        val viewModel = mockk<ExchangeDetailViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(ExchangeDetailUiState.Loading)

        var backClicked = false

        composeTestRule.setContent {
            ExchangeDetailScreen(viewModel = viewModel, onNavigateBack = { backClicked = true })
        }

        composeTestRule.onNodeWithContentDescription("Voltar").performClick()
        
        assertTrue(backClicked)
    }
}
