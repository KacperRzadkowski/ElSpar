package com.team12.ElSpar.data

import android.util.Log
import com.team12.ElSpar.Settings.PriceArea
import com.team12.ElSpar.api.HvaKosterStrommenApiService
import com.team12.ElSpar.exceptions.PriceNotAvailableException
import com.team12.ElSpar.exceptions.NoConnectionException
import java.time.LocalDate
import java.time.LocalDateTime

interface PowerRepository {
    suspend fun getPowerPricesByDate(
        date: LocalDate,
        area: PriceArea
    ): Map<LocalDateTime, Double>
}

class DefaultPowerRepository(
    private val hvaKosterStrommenApiService: HvaKosterStrommenApiService
) : PowerRepository {
    private val localRepo = mutableMapOf<Pair<LocalDate, PriceArea>, Map<LocalDateTime, Double>>()
    //Map with price by date
    override suspend fun getPowerPricesByDate(
        date: LocalDate,
        area: PriceArea
    ): Map<LocalDateTime, Double> {
        val priceData = mutableMapOf<LocalDateTime, Double>()
        val key = Pair(date, area)
        if (localRepo[key] != null) return localRepo[key]!!
        hvaKosterStrommenApiService
            .getPowerPricesByDate(date, area)
            .forEach {
                //If not NO4 multiply with added value tax
                priceData[LocalDateTime.parse(it.time_start.dropLast(6))] = it.NOK_per_kWh * (if(area != PriceArea.NO4) 125 else 100)
            }
        localRepo[key] = priceData
        return priceData
    }
}