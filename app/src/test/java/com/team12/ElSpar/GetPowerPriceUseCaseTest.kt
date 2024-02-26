package com.team12.ElSpar

import com.team12.ElSpar.data.DefaultPowerRepository
import com.team12.ElSpar.domain.GetPowerPriceUseCase
import com.team12.ElSpar.fake.FakeHvaKosterStrommenApiService
import com.team12.ElSpar.model.PricePeriod
import io.ktor.client.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetPowerPriceUseCaseTest {
    private val powerRepository = DefaultPowerRepository(FakeHvaKosterStrommenApiService())
    private val getPowerPriceUseCase: GetPowerPriceUseCase =
        GetPowerPriceUseCase(
            powerRepository = powerRepository,
            getProjectedPowerPriceUseCase = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPowerPriceUseCase_invoke_mapIsNotEmpty() =
        runTest {
            val result =
                getPowerPriceUseCase(
                    period = PricePeriod.DAY,
                    endDate = LocalDate.of(2023, 1, 1),
                    area = Settings.PriceArea.NO1
                )

            assertTrue(result.isNotEmpty())
        }
    @Test
    fun getPowerPriceUseCase_invoke_containsDataForWeek() =
        runTest {
            val result =
                getPowerPriceUseCase(
                    period = PricePeriod.WEEK,
                    endDate = LocalDate.of(2023, 1, 7),
                    area = Settings.PriceArea.NO1
                )
            assertTrue(
                result.size == 7
            )
        }
    @Test
    fun getPowerPriceUseCase_invoke_noPriceDataYearsAhead() =
        runTest{
            val date: LocalDate = LocalDate.now().plusYears(10)
            val result =
                getPowerPriceUseCase(
                    period = PricePeriod.WEEK,
                    endDate = date,
                    area = Settings.PriceArea.NO1
                )
            val time = LocalTime.MIDNIGHT

            assertTrue(
                result[LocalDateTime.of(date,time)] == null,
            )
        }
}

