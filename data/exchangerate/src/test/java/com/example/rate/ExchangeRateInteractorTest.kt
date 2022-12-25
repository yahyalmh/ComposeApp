package com.example.rate

import com.example.rate.model.toExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
internal class ExchangeRateInteractorTest {

    @Mock
    lateinit var rateRepository: ExchangeRateRepositoryImpl
    private lateinit var exchangeRateInteractor: ExchangeRateInteractorImpl

    @BeforeEach
    fun setUp() {
        exchangeRateInteractor = ExchangeRateInteractorImpl(rateRepository)
    }

    @Test
    fun `WHEN fetch rates from interactor THEN return rates`() = runTest {
        val exchangeRatesModel = exchangeRatesModelStub()
        whenever(rateRepository.getExchangeRates()).thenReturn(exchangeRatesModel)

        val actual = exchangeRateInteractor.getRates().first()
        val expected = exchangeRatesModel.toExternalModel().sortedByDescending { it.symbol }

        Assertions.assertEquals(expected, actual)
    }


    @Test
    fun `WHEN fetch live rates THEN return rates periodically`() = runTest {
        val exchangeRatesModel = exchangeRatesModelStub()
        whenever(rateRepository.getExchangeRates()).thenReturn(exchangeRatesModel)

        val actual = exchangeRateInteractor.getLiveRates(3000).take(3).toList()
        val expected = exchangeRatesModel.toExternalModel().sortedByDescending { it.symbol }

        actual.forEach { Assertions.assertEquals(expected, it) }
    }

    @Test
    fun `WHEN fetch a live rate with id THEN return data based on interval`() = runTest {
        val exchangeRateDetailModel = exchangeRateDetailModelStub()
        val exchangeRateId = exchangeRateDetailModel.rateDetail.id
        whenever(rateRepository.getExchangeRate(exchangeRateId)).thenReturn(
            exchangeRateDetailModel
        )

        val actual = exchangeRateInteractor.getLiveRate(exchangeRateId).take(3).toList()
        val expected = exchangeRateDetailModel.toExternalModel()

        actual.forEach { Assertions.assertEquals(expected, it) }
    }
}