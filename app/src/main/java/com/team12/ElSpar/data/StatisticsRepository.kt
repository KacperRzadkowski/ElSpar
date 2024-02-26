package com.team12.ElSpar.data

import com.team12.ElSpar.api.SsbApiService
import com.team12.ElSpar.exceptions.NoConnectionException
import java.time.LocalDate

interface StatisticsRepository {
    suspend fun getCpi(date: LocalDate): Double
}

class SsbStatisticsRepository (
    private val ssbApiService: SsbApiService,
    private var currentCpi: Double = 0.0
) : StatisticsRepository {
    override suspend fun getCpi(
        date: LocalDate
    ): Double {
        if (currentCpi == 0.0) currentCpi = ssbApiService.getCpi(date)/100
        return currentCpi
    }
}