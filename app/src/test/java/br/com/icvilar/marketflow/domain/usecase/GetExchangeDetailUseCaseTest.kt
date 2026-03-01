package br.com.icvilar.marketflow.domain.usecase

import br.com.icvilar.marketflow.domain.model.ExchangeDetail
import br.com.icvilar.marketflow.domain.model.ExchangeURLs
import br.com.icvilar.marketflow.domain.repository.ExchangeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetExchangeDetailUseCaseTest {

    private lateinit var repository: ExchangeRepository
    private lateinit var useCase: GetExchangeDetailUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetExchangeDetailUseCase(repository)
    }

    @Test
    fun `invoke returns success with exchange detail`() = runTest {
        // Arrange
        val detail = ExchangeDetail(
            id = 270,
            name = "Binance",
            logo = "https://example.com/logo.png",
            description = "Leading exchange",
            makerFee = 0.1,
            takerFee = 0.1,
            dateLaunched = "2017-07-14",
            urls = ExchangeURLs(website = listOf("https://binance.com"))
        )
        coEvery { repository.getExchangeDetail("270") } returns Result.success(detail)

        // Act
        val result = useCase("270")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Binance", result.getOrNull()?.name)
        assertEquals(270, result.getOrNull()?.id)
        assertEquals(0.1, result.getOrNull()?.makerFee)
        coVerify(exactly = 1) { repository.getExchangeDetail("270") }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Arrange
        coEvery { repository.getExchangeDetail("999") } returns Result.failure(
            Exception("Exchange not found")
        )

        // Act
        val result = useCase("999")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Exchange not found", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { repository.getExchangeDetail("999") }
    }

    @Test
    fun `invoke passes correct id to repository`() = runTest {
        // Arrange
        val detail = ExchangeDetail(id = 42, name = "Kraken")
        coEvery { repository.getExchangeDetail("42") } returns Result.success(detail)

        // Act
        val result = useCase("42")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Kraken", result.getOrNull()?.name)
        coVerify(exactly = 1) { repository.getExchangeDetail("42") }
    }
}
