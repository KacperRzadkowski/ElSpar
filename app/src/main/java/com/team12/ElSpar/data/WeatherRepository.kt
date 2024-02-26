package com.team12.ElSpar.data

import com.team12.ElSpar.Settings.PriceArea
import com.team12.ElSpar.api.MetApiService
import com.team12.ElSpar.exceptions.NoConnectionException
import java.time.LocalDate
import java.time.LocalDateTime

interface WeatherRepository {
    suspend fun getWeatherDataFromDate(
        date: LocalDate,
        area: PriceArea
    ):Map<WeatherElement, Map<LocalDateTime, Double>>
}

class DefaultWeatherRepository(
    private val frostApiService: MetApiService,
    private val locationForecastApiService: MetApiService,
    private val localRepo: MutableMap<WeatherElement, MutableMap<LocalDateTime, Double>> = mutableMapOf(),
    private var currentAreaInRepo: PriceArea = PriceArea.NO1
):WeatherRepository  {
    override suspend fun getWeatherDataFromDate(
        date: LocalDate,
        area: PriceArea
    ):Map<WeatherElement, Map<LocalDateTime, Double>> {
        if (currentAreaInRepo != area) localRepo.clear()
        if (localRepo.isNotEmpty()) return localRepo

        for (element in WeatherElement.values()) {
            localRepo[element] = (locationForecastApiService
                    .getWeatherData(priceAreaToWeatherLocation(area), element) + frostApiService
                    .getWeatherData(priceAreaToWeatherLocation(area), element, date))
                    .toSortedMap()
        }
        currentAreaInRepo = area
        return localRepo
    }

    private fun priceAreaToWeatherLocation(area: PriceArea): WeatherLocation {
        return when (area) {
            PriceArea.NO2 -> WeatherLocation.STAVANGER
            PriceArea.NO3 -> WeatherLocation.TRONDHEIM
            PriceArea.NO4 -> WeatherLocation.BODØ
            PriceArea.NO5 -> WeatherLocation.BERGEN
            else -> WeatherLocation.OSLO
        }
    }
}

enum class WeatherLocation(val lat: Double, val lon: Double, val station: String) {
    OSLO(59.91, 10.75, "SN18700"),
    STAVANGER(58.96, 5.72, "SN44640"),
    TRONDHEIM(63.43, 10.39, "SN68125"),
    BODØ(67.28, 14.40, "SN82410"),
    BERGEN(60.39, 5.32, "SN50500")
}

enum class WeatherElement(val query: String) {
    TEMPERATURE("air_temperature"),
    PRECIPITATION("sum(precipitation_amount PT1H)"),
    WIND("wind_speed")
}