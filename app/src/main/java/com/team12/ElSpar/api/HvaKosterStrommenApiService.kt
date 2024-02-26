package com.team12.ElSpar.api

import com.team12.ElSpar.Settings
import com.team12.ElSpar.exceptions.PriceNotAvailableException
import com.team12.ElSpar.model.PriceData
import com.team12.ElSpar.exceptions.NoConnectionException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.serialization.*
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDate

private const val TIMEOUT = 10000L

interface HvaKosterStrommenApiService {
    suspend fun getPowerPricesByDate(date: LocalDate, area: Settings.PriceArea)
    : List<PriceData>
}

class DefaultHvaKosterStrommenApiService(
    private val client: HttpClient,
    private val baseURL: String = "https://www.hvakosterstrommen.no/api/v1/prices"
) : HvaKosterStrommenApiService{
    override suspend fun getPowerPricesByDate(date: LocalDate, area: Settings.PriceArea): List<PriceData> {
        return try {
            client.get(
                baseURL +
                        "/" + date.year +
                        "/" + date.monthValue.toString().padStart(2, '0') +
                        "-" + date.dayOfMonth.toString().padStart(2, '0') +
                        "_" + area.name + ".json"
            ) {
                timeout {
                    requestTimeoutMillis = TIMEOUT
                }
            }.body()
        } catch (e: JsonConvertException) {
            throw PriceNotAvailableException()
        } catch (e: UnresolvedAddressException) {
            throw NoConnectionException()
        }
    }
}