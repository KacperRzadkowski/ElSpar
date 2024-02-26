package com.team12.ElSpar.fake

import com.team12.ElSpar.model.PriceData
import java.time.LocalDateTime



//Fake powerprice data
//priceDataMapMVA is the priceDataListTodayFake with moms added
//priceDataListJan contains some data for January and is useful in viewModel testing

object FakePowerDataSource {
    val priceData1 = PriceData(
        NOK_per_kWh = 10 * 0.10,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-30T00:00:00+02:00",
        time_end = "2023-01-30T01:00:00+02:00"
    )
    val priceData2 = PriceData(
        NOK_per_kWh = 10 * 0.20,
        EUR_per_kWh = 0.20,
        EXR = 10.0,
        time_start = "2023-01-30T01:00:00+02:00",
        time_end = "2023-01-30T02:00:00+02:00"
    )
    val priceDataJan1 = PriceData(
        NOK_per_kWh = 1.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-01T00:00:00+02:00",
        time_end = "2023-01-01T01:00:00+02:00"
    )
    val priceDataJan2 = PriceData(
        NOK_per_kWh = 2.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-02T00:00:00+02:00",
        time_end = "2023-01-02T01:00:00+02:00"
    )
    val priceDataJan3 = PriceData(
        NOK_per_kWh = 3.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-03T00:00:00+02:00",
        time_end = "2023-01-03T01:00:00+02:00"
    )
    val priceDataJan4 = PriceData(
        NOK_per_kWh = 4.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-04T00:00:00+02:00",
        time_end = "2023-01-04T01:00:00+02:00"
    )
    val priceDataJan5 = PriceData(
        NOK_per_kWh = 5.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-05T00:00:00+02:00",
        time_end = "2023-01-05T01:00:00+02:00"
    )
    val priceDataJan6 = PriceData(
        NOK_per_kWh = 6.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-06T00:00:00+02:00",
        time_end = "2023-01-06T01:00:00+02:00"
    )
    val priceDataJan7 = PriceData(
        NOK_per_kWh = 7.0,
        EUR_per_kWh = 0.10,
        EXR = 10.0,
        time_start = "2023-01-07T00:00:00+02:00",
        time_end = "2023-01-07T01:00:00+02:00"
    )

    var priceDataListJan = mutableListOf<PriceData>(
        priceData1,
        priceData2,
        priceDataJan1,
        priceDataJan2,
        priceDataJan3,
        priceDataJan4,
        priceDataJan5,
        priceDataJan6,
        priceDataJan7
    )
    val priceDataListTodayFake = listOf<PriceData>(
        priceData1,
        priceData2
    )
    //this list is the result generated after adding moms to todays pricedata
    var priceDataMapMVA = mapOf<LocalDateTime, Double>(
        LocalDateTime.of(2023, 1, 30, 0, 0) to  priceData1.NOK_per_kWh*125,
        LocalDateTime.of(2023, 1, 30, 1, 0) to  priceData2.NOK_per_kWh*125,
    )


}