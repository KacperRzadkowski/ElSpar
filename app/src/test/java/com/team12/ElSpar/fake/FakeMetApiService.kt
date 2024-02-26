package com.team12.ElSpar.fake

import com.team12.ElSpar.api.MetApiService
import com.team12.ElSpar.data.WeatherElement
import com.team12.ElSpar.data.WeatherLocation
import java.time.LocalDate
import java.time.LocalDateTime

//Mock up for tests. NOT IMPLEMENTED YET.
class FakeMetApiService: MetApiService() {
    override suspend fun getWeatherData(
        location: WeatherLocation,
        element: WeatherElement,
        date: LocalDate
    ): Map<LocalDateTime, Double> {
        TODO("Not yet implemented")
    }
}

