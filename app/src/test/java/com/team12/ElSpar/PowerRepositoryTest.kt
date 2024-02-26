package com.team12.ElSpar

import com.team12.ElSpar.data.DefaultPowerRepository
import com.team12.ElSpar.fake.FakeHvaKosterStrommenApiService
import com.team12.ElSpar.fake.FakePowerDataSource
import com.team12.ElSpar.model.PriceData
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime


@OptIn(ExperimentalCoroutinesApi::class)
class PowerRepositoryTest {
    private val powerRepository = DefaultPowerRepository(FakeHvaKosterStrommenApiService())

    @Test
    fun powerRepository_getPowerPricesByDate_IsEqualMap() =
        runTest{
            val date = LocalDateTime.of(2023, 1, 30, 0, 0)
            val result = powerRepository.getPowerPricesByDate ( date.toLocalDate(), Settings.PriceArea.NO1)
            assertEquals(
                FakePowerDataSource.priceDataMapMVA,
                result
            )
    }

    @Test
    fun powerRepository_getPowerPricesByDate_cachesData() =
        runTest{
            val date1 = "1900-01-30T01:00:00+02:00"
            val date2 = "1900-01-30T02:00:00+02:00"
            val date: LocalDateTime = LocalDateTime.of(1900, 1, 30, 1, 0)

            val area: Settings.PriceArea = Settings.PriceArea.NO1
            val priceData = PriceData(
                NOK_per_kWh = 3.0,
                EUR_per_kWh = 0.30,
                EXR = 10.0,
                time_start = date1,
                time_end = date2
            )

            //Add new data for DataSource
            FakePowerDataSource.priceDataListJan.apply {
                add(
                    priceData
                )
            }
            //load the new data to the localRepo by calling getPowerPricesByDate
            var result = powerRepository.getPowerPricesByDate (
                date.toLocalDate(),
                area
            )
            //Verify that data arrived
            assertEquals(
                priceData.NOK_per_kWh * 125,
                result[date]
            )
            //remove the new data from DataSource
            FakePowerDataSource.priceDataListJan.apply{
                remove(priceData)
            }

            //if powerRepository still manages to find data for the date, the data must be in cache.
            result = powerRepository.getPowerPricesByDate (
                date.toLocalDate(),
                Settings.PriceArea.NO1
            )
            assertEquals(
                priceData.NOK_per_kWh * 125,
                result[date]
            )
        }

    @Test
    fun powerRepository_getPowerPricesByDate_convertsToOreAndAddsMoms() =
    runTest{
        val priceData = FakePowerDataSource.priceDataJan1
        val dateTime = LocalDateTime.parse(priceData.time_start.dropLast(6))

        val result = powerRepository.getPowerPricesByDate(
            dateTime.toLocalDate(),
            Settings.PriceArea.NO1
        )

        assertTrue(
            result[dateTime] == priceData.NOK_per_kWh *125
                )
    }
}