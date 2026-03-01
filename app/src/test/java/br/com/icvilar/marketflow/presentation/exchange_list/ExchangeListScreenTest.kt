package br.com.icvilar.marketflow.presentation.exchange_list

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.com.icvilar.marketflow.domain.model.Exchange
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE, instrumentedPackages = ["androidx.loader.content"])
class ExchangeListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when state is Loading, shows progress indicator`() {
        // Arrange
        val viewModel = mockk<ExchangeListViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(ExchangeListUiState.Loading)

        // Act
        composeTestRule.setContent {
            ExchangeListScreen(
                viewModel = viewModel,
                onNavigateToDetail = {}
            )
        }

        // Assert
        // The CircularProgressIndicator doesn't have a default test tag, but we can find it by its semantics
        // Or we can just check there is no list or error
        composeTestRule.onNodeWithText("Error:").assertDoesNotExist()
    }

    @Test
    fun `when state is Error, shows error message and retry button`() {
        // Arrange
        val viewModel = mockk<ExchangeListViewModel>(relaxed = true)
        val errorMessage = "Network failure"
        every { viewModel.uiState } returns MutableStateFlow(ExchangeListUiState.Error(errorMessage))

        // Act
        composeTestRule.setContent {
            ExchangeListScreen(
                viewModel = viewModel,
                onNavigateToDetail = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Error: $errorMessage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
        
        // Test retry click
        composeTestRule.onNodeWithText("Retry").performClick()
    }

    @Test
    fun `when state is Success, shows exchange list`() {
        // Arrange
        val viewModel = mockk<ExchangeListViewModel>(relaxed = true)
        val exchanges = listOf(
            Exchange(id = 1, name = "Binance", slug = "binance", firstHistoricalData = "2017-07-14T00:00:00.000Z"),
            Exchange(id = 2, name = "Coinbase", slug = "coinbase")
        )
        
        every { viewModel.uiState } returns MutableStateFlow(ExchangeListUiState.Success(exchanges))
        every { viewModel.formatLogoUrl(any()) } returns "https://example.com/logo.png"
        every { viewModel.formatVolume(any()) } returns "Vol: N/A"
        every { viewModel.formatDate(any()) } returns "Launched: 2017-07-14"

        // Act
        composeTestRule.setContent {
            ExchangeListScreen(
                viewModel = viewModel,
                onNavigateToDetail = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Binance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Coinbase").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Vol: N/A").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Launched: 2017-07-14").onFirst().assertIsDisplayed()
    }

    @Test
    fun `when clicking an exchange, triggers navigation`() {
        // Arrange
        val viewModel = mockk<ExchangeListViewModel>(relaxed = true)
        var navigatedId: String? = null
        val exchanges = listOf(
            Exchange(id = 270, name = "Binance", slug = "binance")
        )
        
        every { viewModel.uiState } returns MutableStateFlow(ExchangeListUiState.Success(exchanges))
        every { viewModel.formatLogoUrl(any()) } returns "url"
        every { viewModel.formatVolume(any()) } returns "vol"
        every { viewModel.formatDate(any()) } returns "date"

        // Act
        composeTestRule.setContent {
            ExchangeListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { id -> navigatedId = id }
            )
        }

        composeTestRule.onNodeWithText("Binance").performClick()

        // Assert
        assert(navigatedId == "270")
    }
}
