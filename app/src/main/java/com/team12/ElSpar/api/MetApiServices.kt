package com.team12.ElSpar.api

import com.team12.ElSpar.data.WeatherElement
import com.team12.ElSpar.data.WeatherLocation
import com.team12.ElSpar.exceptions.NoConnectionException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val API_KEY = "48380fc6-bed9-4253-a039-488eb2431968"
private const val TIMEOUT = 15000L

abstract class MetApiService(
    protected val result: MutableMap<LocalDateTime, Double> = mutableMapOf(),
    protected val baseUrl: String = "https://gw-uio.intark.uh-it.no/in2000/"
) {
    abstract suspend fun getWeatherData(
        location: WeatherLocation,
        element: WeatherElement,
        date: LocalDate = LocalDate.now(),
    ): Map<LocalDateTime, Double>
}

class FrostApiService(
    private val client: HttpClient,
    private val endpoint: String = "frostapi/observations/v0.jsonld",
) : MetApiService() {
    override suspend fun getWeatherData(
        location: WeatherLocation,
        element: WeatherElement,
        date: LocalDate,
    ): Map<LocalDateTime, Double> {
        val response: JsonObject = try {
            request(
                client = client,
                location = location,
                element = element,
                referenceTime =
                DateTimeFormatter
                    .ofPattern("yyyy-MM-dd'T'0/")
                    .format(date) +
                        latest(
                            client = client,
                            location = location,
                            element = element
                        )
            ).apply { if (status.value !in 200..299) return result }.body()
        } catch (e: UnresolvedAddressException) {
            throw NoConnectionException()
        }

        response["data"]?.jsonArray
            ?.forEach {
                it.jsonObject.run {
                    result[LocalDateTime.parse(
                        get("referenceTime")?.jsonPrimitive
                            ?.content
                            ?.dropLast(5)
                    ) ?:LocalDateTime.now()] =
                        get("observations")?.jsonArray
                        ?.first()?.jsonObject
                        ?.get("value")?.jsonPrimitive
                        ?.doubleOrNull
                        ?: 0.0
                }
            }
        return result
    }

    private suspend fun latest(
        client: HttpClient,
        location: WeatherLocation,
        element: WeatherElement
    ): String {
        val defaultVal = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'23")
            .format(LocalDateTime.now().minusDays(1L))
        val response: JsonObject = request(
            client = client,
            location = location,
            element = element,
            referenceTime = "latest"
        ).apply { if (status.value !in 200..299) return defaultVal }.body()

        return response["data"]?.jsonArray
            ?.first()?.jsonObject
            ?.get("referenceTime")?.jsonPrimitive
            ?.toString()?.dropLast(11)
            ?: defaultVal
    }

    private suspend fun request(
        client: HttpClient,
        location: WeatherLocation,
        element: WeatherElement,
        referenceTime: String
    ): HttpResponse {
        return client.get(baseUrl + endpoint) {
            headers { append("X-Gravitee-API-Key", API_KEY) }
            timeout {
                requestTimeoutMillis = TIMEOUT
            }
            url {
                parameters.append("levels", "default")
                parameters.append("timeresolutions", "PT1H")
                parameters.append("qualities", "0")
                parameters.append("sources", location.station)
                parameters.append("elements", element.query)
                parameters.append("referencetime", referenceTime)
            }
        }
    }
}

class LocationForecastApiService(
    private val client: HttpClient,
    private val endpoint: String = "weatherapi/locationforecast/2.0/compact",
) : MetApiService() {
    override suspend fun getWeatherData(
        location: WeatherLocation,
        element: WeatherElement,
        date: LocalDate,
    ): Map<LocalDateTime, Double> {
        val response: JsonObject = try {
            client.get(baseUrl + endpoint) {
                headers { append("X-Gravitee-API-Key", API_KEY) }
                timeout {
                    requestTimeoutMillis = TIMEOUT
                }
                url {
                    parameters.append("lat", "${location.lat}")
                    parameters.append("lon", "${location.lon}")
                }
            }.apply { if (status.value !in 200..299) return result }.body()
        } catch (e: UnresolvedAddressException) {
            throw NoConnectionException()
        }

        response["properties"]?.jsonObject
            ?.get("timeseries")?.jsonArray
            ?.forEach {
                it.jsonObject.let { observation ->
                    result[LocalDateTime.parse(
                        observation["time"]?.jsonPrimitive
                            ?.content
                            ?.dropLast(1)
                    ) ?:LocalDateTime.now()] =
                        observation["data"]?.jsonObject.let { data ->
                        if (element == WeatherElement.PRECIPITATION) {
                            data?.get("next_1_hours")?.jsonObject
                                ?.get("details")?.jsonObject
                                ?.get("precipitation_amount")?.jsonPrimitive
                                ?.doubleOrNull
                                ?: 0.0
                        } else {
                            data?.get("instant")?.jsonObject
                                ?.get("details")?.jsonObject
                                ?.get(element.query)?.jsonPrimitive
                                ?.doubleOrNull
                                ?: 0.0
                        }
                    }
                }
            }
        return result
    }
}
