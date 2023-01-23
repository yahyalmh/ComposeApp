package com.example.rate

import com.example.rate.api.ExchangeRateApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class ExchangeRateRepositoryTest {

    @Mock
    lateinit var exchangeRateApi: ExchangeRateApi
    lateinit var exchangeRateRepository: ExchangeRateRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        exchangeRateRepository = ExchangeRateRepositoryImpl(exchangeRateApi)
    }

    @Test
    fun `WHEN exchange rates fetched from repository THEN work well`() = runTest {
        // given
        val exchangeRatesModelStub = exchangeRatesModelStub()
        whenever(exchangeRateApi.getExchangeRates()).thenReturn(exchangeRatesModelStub)

        // when
        val result = exchangeRateRepository.getExchangeRates()

        // then
        Assertions.assertEquals(exchangeRatesModelStub, result)
    }

    @Test
    fun `WHEN an exchange rate fetched from repository THEN work well`() = runTest {
        // given
        val exchangeRateDetailModel = exchangeRateDetailModelStub()
        val exchangeRateId = exchangeRateDetailModel.rateDetail.id
        whenever(
            exchangeRateApi
                .getExchangeRate(exchangeRateId)
        ).thenReturn(
            exchangeRateDetailModel
        )

        // when
        val result = exchangeRateRepository.getExchangeRate(exchangeRateId)

        // then
        Assertions.assertEquals(exchangeRateDetailModel, result)
    }

}