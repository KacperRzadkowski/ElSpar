package com.team12.ElSpar.domain

import com.team12.ElSpar.Settings.PriceArea
import com.team12.ElSpar.exceptions.PriceNotAvailableException
import com.team12.ElSpar.data.PowerRepository
import com.team12.ElSpar.model.PricePeriod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class GetPowerPriceUseCase (
    private val powerRepository: PowerRepository,
    private val getProjectedPowerPriceUseCase: GetProjectedPowerPriceUseCase?,
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO //for testDisp. injection

) {
    suspend operator fun invoke(
        period: PricePeriod,
        endDate: LocalDate,
        area: PriceArea,
    ): Map<LocalDateTime, Double> = withContext(iODispatcher) {
        val priceData = mutableMapOf<LocalDateTime, Double>()
        val startDate = endDate.minusDays(period.days - 1L)
        for (i in 0 until period.days) {

            //for each date from start date:
            startDate.plusDays(i.toLong()).let { date ->
                val powerPrice = try {
                    /*check if its a future date after tomorrows date OR
                    date is set to exactly tomorrow and clock has passed not 13:00 today.*/
                    if (getProjectedPowerPriceUseCase != null &&
                        (date > LocalDate.now().plusDays(1)
                                || (date == LocalDate.now().plusDays(1)
                                && LocalDateTime.now().hour < 13))) {
                        getProjectedPowerPriceUseCase.let { it(date, area) }
                    } else {
                        powerRepository.getPowerPricesByDate(date, area)
                    }
                } catch (e: PriceNotAvailableException) {
                    //exception if price is not available, invoke projected price use case to get an estimate
                    getProjectedPowerPriceUseCase?.let { it( date, area)}
                }

                if (powerPrice != null) {
                    priceData += powerPrice
                }
            }
        }
        priceData
    }
}


