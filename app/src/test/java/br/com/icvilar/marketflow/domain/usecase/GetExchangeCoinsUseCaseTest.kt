package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.Asset
import br.com.icvilar.marketflow.domain.model.AssetCurrency
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetExchangeCoinsUseCaseTest {

    private lateinit var repository: ExchangeRepository
    private lateinit var useCase: GetExchangeCoinsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetExchangeCoinsUseCase(repository)
    }

    @Test
    fun `invoke returns success with coin list`() = runTest {
        // Arrange
        val coins = listOf(
            Asset(currency = AssetCurrency(name = "Bitcoin", symbol = "BTC", priceUsd = 1.0)),
            Asset(currency = AssetCurrency(name = "Ethereum", symbol = "ETH", priceUsd = 2.0))
        )
        coEvery { repository.getExchangeCoins("270") } returns Result.success(coins)

        // Act
        val result = useCase("270")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Bitcoin", result.getOrNull()?.first()?.currency?.name)
        assertEquals("ETH", result.getOrNull()?.last()?.currency?.symbol)
        coVerify(exactly = 1) { repository.getExchangeCoins("270") }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Arrange
        coEvery { repository.getExchangeCoins("270") } returns Result.failure(
            Exception("Service unavailable")
        )

        // Act
        val result = useCase("270")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Service unavailable", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.getExchangeCoins("270") }
    }

    @Test
    fun `invoke returns empty list when no coins`() = runTest {
        // Arrange
        coEvery { repository.getExchangeCoins("42") } returns Result.success(emptyList())

        // Act
        val result = useCase("42")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `invoke passes correct id to repository`() = runTest {
        // Arrange
        val coins = listOf(
            Asset(currency = AssetCurrency(name = "Bitcoin", symbol = "BTC", priceUsd = 1.0))
        )
        coEvery { repository.getExchangeCoins("123") } returns Result.success(coins)

        // Act
        val result = useCase("123")

        // Assert
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.getExchangeCoins("123") }
    }
}
