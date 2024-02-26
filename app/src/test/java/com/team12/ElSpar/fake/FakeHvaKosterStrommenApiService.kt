package com.team12.ElSpar.fake

import com.team12.ElSpar.Settings
import com.team12.ElSpar.api.HvaKosterStrommenApiService
import com.team12.ElSpar.exceptions.PriceNotAvailableException
import com.team12.ElSpar.model.PriceData
import java.time.LocalDate
import java.time.LocalDateTime

class FakeHvaKosterStrommenApiService: HvaKosterStrommenApiService {
    override suspend fun getPowerPricesByDate(date: LocalDate, area: Settings.PriceArea): List<PriceData> {
        val priceDataList = mutableListOf<PriceData>()

        //Since the fake datasource dosent contain data for today - 30 days, we instead return another list of data from 31.01.23
        if(date.plusDays(31) >= LocalDate.now()){
            return FakePowerDataSource.priceDataListTodayFake
        }
        // Iterate over the data in FakePowerDataSource.priceDataList and add matching items to priceDataList
        FakePowerDataSource.priceDataListJan.forEach { priceData ->
            val priceDateTime = LocalDateTime.parse(priceData.time_start.dropLast(6))
            if (priceDateTime.toLocalDate() == date
            //&& area == Settings.PriceArea.NO1
            ) {
                priceDataList.add(priceData)
            }
        }

        // If no data was found, throw a PriceNotAvailableException
        if (priceDataList.isEmpty()) {
            throw PriceNotAvailableException()
        }

        return priceDataList
    }
}