package br.com.icvilar.marketflow.data.repository

import br.com.icvilar.marketflow.data.remote.CoinMarketCapApi
import br.com.icvilar.marketflow.domain.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ExchangeRepositoryImplTest {

    private lateinit var api: CoinMarketCapApi
    private lateinit var repository: ExchangeRepositoryImpl

    private val successStatus = APIStatus(
        timestamp = "2024-01-01T00:00:00.000Z",
        errorCode = 0,
        errorMessage = null
    )

    private val errorStatus = APIStatus(
        timestamp = "2024-01-01T00:00:00.000Z",
        errorCode = 1001,
        errorMessage = "API key is invalid"
    )

    @Before
    fun setup() {
        api = mockk()
        repository = ExchangeRepositoryImpl(api)
    }

    // ==========================================
    // getExchanges()
    // ==========================================

    @Test
    fun `getExchanges returns success when API responds successfully`() = runTest {
        // Arrange
        val exchanges = listOf(
            Exchange(id = 1, name = "Binance", slug = "binance"),
            Exchange(id = 2, name = "Coinbase", slug = "coinbase")
        )
        val exchangeResponse = ExchangeResponse(data = exchanges, status = successStatus)
        coEvery { api.getExchanges(any(), any()) } returns Response.success(exchangeResponse)

        // Act
        val result = repository.getExchanges()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Binance", result.getOrNull()?.first()?.name)
        assertEquals("Coinbase", result.getOrNull()?.last()?.name)
        coVerify(exactly = 1) { api.getExchanges(any(), any()) }
    }

    @Test
    fun `getExchanges returns failure when API returns HTTP error`() = runTest {
        // Arrange
        coEvery { api.getExchanges(any(), any()) } returns Response.error(
            401, "Unauthorized".toResponseBody(null)
        )

        // Act
        val result = repository.getExchanges()

        // Assert
        assertTrue(result.isFailure)
        val errorMsg = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected HTTP Error with code 401, got: $errorMsg", errorMsg.contains("401"))
    }

    @Test
    fun `getExchanges returns failure when API status has error code`() = runTest {
        // Arrange
        val exchangeResponse = ExchangeResponse(data = emptyList(), status = errorStatus)
        coEvery { api.getExchanges(any(), any()) } returns Response.success(exchangeResponse)

        // Act
        val result = repository.getExchanges()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("API key is invalid", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchanges returns failure when exception is thrown`() = runTest {
        // Arrange
        coEvery { api.getExchanges(any(), any()) } throws RuntimeException("Network error")

        // Act
        val result = repository.getExchanges()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchanges returns empty list when API returns empty data`() = runTest {
        // Arrange
        val exchangeResponse = ExchangeResponse(data = emptyList(), status = successStatus)
        coEvery { api.getExchanges(any(), any()) } returns Response.success(exchangeResponse)

        // Act
        val result = repository.getExchanges()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    // ==========================================
    // getExchangeDetail()
    // ==========================================

    @Test
    fun `getExchangeDetail returns success when exchange is found`() = runTest {
        // Arrange
        val detail = ExchangeDetail(
            id = 270,
            name = "Binance",
            logo = "https://example.com/logo.png",
            description = "Binance Exchange",
            makerFee = 0.1,
            takerFee = 0.1,
            dateLaunched = "2017-07-14",
            urls = ExchangeURLs(website = listOf("https://binance.com"))
        )
        val response = ExchangeInfoResponse(
            data = mapOf("270" to detail),
            status = successStatus
        )
        coEvery { api.getExchangeDetails("270") } returns Response.success(response)

        // Act
        val result = repository.getExchangeDetail("270")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Binance", result.getOrNull()?.name)
        assertEquals(0.1, result.getOrNull()?.makerFee)
        assertEquals("2017-07-14", result.getOrNull()?.dateLaunched)
    }

    @Test
    fun `getExchangeDetail returns failure when exchange ID not in map`() = runTest {
        // Arrange
        val response = ExchangeInfoResponse(
            data = emptyMap(),
            status = successStatus
        )
        coEvery { api.getExchangeDetails("999") } returns Response.success(response)

        // Act
        val result = repository.getExchangeDetail("999")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Exchange details not found in payload.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeDetail returns failure when API returns HTTP error`() = runTest {
        // Arrange
        coEvery { api.getExchangeDetails("270") } returns Response.error(
            500, "Internal Server Error".toResponseBody(null)
        )

        // Act
        val result = repository.getExchangeDetail("270")

        // Assert
        assertTrue(result.isFailure)
        val errorMsg = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected HTTP Error with code 500, got: $errorMsg", errorMsg.contains("500"))
    }

    @Test
    fun `getExchangeDetail returns failure when API status has error code`() = runTest {
        // Arrange
        val response = ExchangeInfoResponse(data = emptyMap(), status = errorStatus)
        coEvery { api.getExchangeDetails("270") } returns Response.success(response)

        // Act
        val result = repository.getExchangeDetail("270")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("API key is invalid", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeDetail returns failure when network exception`() = runTest {
        // Arrange
        coEvery { api.getExchangeDetails(any()) } throws RuntimeException("Timeout")

        // Act
        val result = repository.getExchangeDetail("270")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Timeout", result.exceptionOrNull()?.message)
    }

    // ==========================================
    // getExchangeCoins()
    // ==========================================

    @Test
    fun `getExchangeCoins returns success with coin list`() = runTest {
        // Arrange
        val coins = listOf(
            Coin(id = 1, name = "Bitcoin", symbol = "BTC", slug = "bitcoin", isActive = 1, rank = 1),
            Coin(id = 2, name = "Ethereum", symbol = "ETH", slug = "ethereum", isActive = 1, rank = 2)
        )
        val coinResponse = CoinResponse(data = coins, status = successStatus)
        coEvery { api.getExchangeAssets("270") } returns Response.success(coinResponse)

        // Act
        val result = repository.getExchangeCoins("270")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Bitcoin", result.getOrNull()?.first()?.name)
        assertEquals("ETH", result.getOrNull()?.last()?.symbol)
    }

    @Test
    fun `getExchangeCoins returns failure when HTTP error`() = runTest {
        // Arrange
        coEvery { api.getExchangeAssets("270") } returns Response.error(
            403, "Forbidden".toResponseBody(null)
        )

        // Act
        val result = repository.getExchangeCoins("270")

        // Assert
        assertTrue(result.isFailure)
        val errorMsg = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected HTTP Error with code 403, got: $errorMsg", errorMsg.contains("403"))
    }

    @Test
    fun `getExchangeCoins returns failure when API status has error`() = runTest {
        // Arrange
        val coinResponse = CoinResponse(data = emptyList(), status = errorStatus)
        coEvery { api.getExchangeAssets("270") } returns Response.success(coinResponse)

        // Act
        val result = repository.getExchangeCoins("270")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("API key is invalid", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeCoins returns failure when exception thrown`() = runTest {
        // Arrange
        coEvery { api.getExchangeAssets(any()) } throws RuntimeException("Connection refused")

        // Act
        val result = repository.getExchangeCoins("270")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Connection refused", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getExchangeCoins returns empty list when no coins available`() = runTest {
        // Arrange
        val coinResponse = CoinResponse(data = emptyList(), status = successStatus)
        coEvery { api.getExchangeAssets("270") } returns Response.success(coinResponse)

        // Act
        val result = repository.getExchangeCoins("270")

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }
}
