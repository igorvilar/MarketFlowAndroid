package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.Exchange
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetExchangesUseCaseTest {

    private lateinit var repository: ExchangeRepository
    private lateinit var useCase: GetExchangesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetExchangesUseCase(repository)
    }

    @Test
    fun `invoke returns success with exchange list from repository`() = runTest {
        // Arrange
        val exchanges = listOf(
            Exchange(id = 1, name = "Binance", slug = "binance"),
            Exchange(id = 2, name = "Coinbase", slug = "coinbase")
        )
        coEvery { repository.getExchanges() } returns Result.success(exchanges)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Binance", result.getOrNull()?.first()?.name)
        coVerify(exactly = 1) { repository.getExchanges() }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Arrange
        coEvery { repository.getExchanges() } returns Result.failure(Exception("Network error"))

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.getExchanges() }
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Arrange
        coEvery { repository.getExchanges() } returns Result.success(emptyList())

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }
}
