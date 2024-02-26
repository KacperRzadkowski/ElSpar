package com.team12.ElSpar.domain

import com.team12.ElSpar.Settings.PriceArea
import com.team12.ElSpar.data.PowerRepository
import com.team12.ElSpar.data.StatisticsRepository
import com.team12.ElSpar.data.WeatherElement
import com.team12.ElSpar.data.WeatherRepository
import com.team12.ElSpar.exceptions.NoConnectionException
import com.team12.ElSpar.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.*

//TRAINING VALUES - for normalization
//mean values
private const val TEMP_MEAN = 7.702426
private const val PREC_MEAN = 0.097141
private const val WIND_MEAN = 2.733666
private const val PRICE_MEAN = 0.272630
private const val CPI_MEAN = 1.227497
private const val DAY_SIN_MEAN = 0.000122
private const val DAY_COS_MEAN = -0.000051
private const val YEAR_SIN_MEAN = 0.034228
private const val YEAR_COS_MEAN = -0.027152

//standard deviation
private const val TEMP_STD = 8.110974
private const val PREC_STD = 0.501262
private const val WIND_STD = 1.649350
private const val PRICE_STD = 0.136227
private const val CPI_STD = 0.210433
private const val DAY_SIN_STD = 0.707097
private const val DAY_COS_STD = 0.707131
private const val YEAR_SIN_STD = 0.704098
private const val YEAR_COS_STD =  0.708772

private const val WARMUP = 48 //HOURS BEHIND
private const val PROJECTION = 24 //HOURS AHEAD

private const val DAY = 24 * 60 * 60
private const val YEAR = 365.2425 * DAY

class GetProjectedPowerPriceUseCase(
    private val powerRepository: PowerRepository,
    private val weatherRepository: WeatherRepository,
    private val statisticsRepository: StatisticsRepository,
    private val model: Model,
    private var price: MutableMap<LocalDateTime, Double> = mutableMapOf(),
    private var currentAreaInRepo: PriceArea = PriceArea.NO1
) {
    suspend operator fun invoke(
        date: LocalDate,
        area: PriceArea,
    ): Map<LocalDateTime, Double> {
        val warmupTime = List(WARMUP) { date.atStartOfDay().minusHours(it.toLong()+1) }.asReversed()
        val projectionTime = List(PROJECTION) { date.atStartOfDay().plusHours(it.toLong()) }
        val time = (warmupTime + projectionTime).toSortedSet().toList()

        if (price.keys.containsAll(projectionTime)) {
            if (area == currentAreaInRepo) return price.filterKeys { it.toLocalDate() == date }
            else price.clear()
        }

        val weather = weatherRepository.getWeatherDataFromDate(time.first().toLocalDate(), area)
        val currentCpi = statisticsRepository.getCpi(time.last().toLocalDate())

        if (!price.keys.containsAll(warmupTime)) {
            for (i in (WARMUP / 24) downTo 1) {
                price += powerRepository.getPowerPricesByDate(date.minusDays(i.toLong()), area)
            }
        }
        currentAreaInRepo = area

        //Prepare input data
        val data: Map<String, MutableMap<LocalDateTime, Double?>> = mapOf(
            "temp" to time.zip(List(time.size) {
                weather[WeatherElement.TEMPERATURE]
                    ?.getOrDefault(time[it], 0.0) }).toMap().toMutableMap(),
            "prec" to time.zip(List(time.size) {
                weather[WeatherElement.PRECIPITATION]
                    ?.getOrDefault(time[it], 0.0) }).toMap().toMutableMap(),
            "wind" to time.zip(List(time.size) {
                weather[WeatherElement.WIND]
                    ?.getOrDefault(time[it], 0.0) }).toMap().toMutableMap(),
            "price" to time.zip(List(time.size) {
                price.getOrDefault(time[it], 0.0)/125 }).toMap().toMutableMap(),
            "cpi" to time.zip(List(time.size) { currentCpi }).toMap().toMutableMap(),
            "daySin" to time.zip(time.map {
                sin(it.toEpochSecond(ZoneOffset.UTC) * (2 * PI / DAY)) }).toMap().toMutableMap(),
            "dayCos" to time.zip(time.map {
                cos(it.toEpochSecond(ZoneOffset.UTC) * (2 * PI / DAY)) }).toMap().toMutableMap(),
            "yearSin" to time.zip(time.map {
                sin(it.toEpochSecond(ZoneOffset.UTC) * (2 * PI / YEAR)) }).toMap().toMutableMap(),
            "yearCos" to time.zip(time.map {
                cos(it.toEpochSecond(ZoneOffset.UTC) * (2 * PI / YEAR)) }).toMap().toMutableMap()
        )

        normalize(data)

        projectionTime.forEach { targetTime ->
            val startTime = targetTime.minusHours(WARMUP.toLong())

            //Prepare input ByteBuffer and insert data
            val byteBuffer = ByteBuffer.allocate(WARMUP * data.size * 4)
            byteBuffer.order(ByteOrder.nativeOrder())

            time.filter { it >= startTime && it < targetTime }
                .forEach { timeStep ->
                    data.forEach { (_, map) ->
                        byteBuffer.putFloat(map[timeStep]?.toFloat() ?: 0F)
                    }
                }

            byteBuffer.rewind()

            // Creates inputs for reference
            val inputFeature0 = TensorBuffer
                .createFixedSize(intArrayOf(1, WARMUP, data.size), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val projectedPrice = outputFeature0.floatArray.first().toDouble()
                .let {
                    if (it.toString() != "NaN") it
                    else 0.0
                }

            this.price[targetTime] = denormalizePrice(projectedPrice)*125
            data["price"]?.set(targetTime, projectedPrice)
        }

        return this.price.filterKeys { it.toLocalDate() == date }
    }

    private fun normalize(data: Map<String, MutableMap<LocalDateTime, Double?>>) {
        val mean = listOf(
            TEMP_MEAN, PREC_MEAN, WIND_MEAN, PRICE_MEAN, CPI_MEAN,
            DAY_SIN_MEAN, DAY_COS_MEAN, YEAR_SIN_MEAN, YEAR_COS_MEAN
        )

        val std = listOf(
            TEMP_STD, PREC_STD, WIND_STD, PRICE_STD, CPI_STD,
            DAY_SIN_STD, DAY_COS_STD, YEAR_SIN_STD, YEAR_COS_STD
        )

        data.entries.forEachIndexed { index, ( _ , map ) ->
            map.toSortedMap().forEach {
                map[it.key] = (it.value?.minus(mean[index]))?.div(std[index])
            }
        }
    }

    private fun denormalizePrice(
        data: Double,
    ): Double {
        return (data * PRICE_STD) + PRICE_MEAN
    }
}