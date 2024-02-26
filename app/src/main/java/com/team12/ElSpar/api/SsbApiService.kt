package com.team12.ElSpar.api

import android.util.Log
import com.team12.ElSpar.exceptions.NoConnectionException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDate

//Henter konsumprisindeksen(KPI-JA) for energivarer fra Statistisk Sentralbyrå

private const val TIMEOUT = 8000L
private const val DEFAULT_CPI = 100.0
private const val ENERGIVARER = "JA_B112"

interface SsbApiService {
    suspend fun getCpi(date: LocalDate): Double
}

class DefaultSsbApiService(
    private val client: HttpClient,
    private val table: String = "11119",
    private val url: String = "https://data.ssb.no/api/v0/no/table/${table}/"
): SsbApiService {
    override suspend fun getCpi(
        date: LocalDate
    ): Double {
        val year = date.year.toString()
        val month = (date.monthValue-2).toString().padStart(2, '0')
        try {
            val response: JsonObject = try {
                client.post(url) {
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        QueryObject(
                            listOf(
                                Query(
                                    code = "Leveringssektor",
                                    selection = Selection(
                                        values = listOf(ENERGIVARER)
                                    )
                                ),
                                Query(
                                    code = "ContentsCode",
                                    selection = Selection(
                                        values = listOf("KPIJustIndMnd")
                                    )
                                ),
                                Query(
                                    code = "Tid",
                                    selection = Selection(
                                        values = listOf(
                                            "${year}M${month}"
                                        )
                                    )
                                )
                            )
                        )
                    )
                }.apply {
                    if (status.value !in 200..299) return DEFAULT_CPI
                }.body()
            } catch (e: UnresolvedAddressException) {
                throw NoConnectionException()
            }

            return response["value"]
                ?.jsonArray
                ?.first()
                ?.jsonPrimitive
                ?.doubleOrNull
                ?: DEFAULT_CPI
        } catch (e: HttpRequestTimeoutException) {
            return DEFAULT_CPI
        } catch (e: ConnectTimeoutException) {
            return DEFAULT_CPI
        } catch (e: SocketTimeoutException) {
            return DEFAULT_CPI
        }
    }
}

@Serializable
private data class QueryObject(
    val query: List<Query>,
    val response: Response = Response()
)

@Serializable
private data class Query(
    val code: String,
    val selection: Selection
)

@Serializable
private data class Selection(
    val values: List<String>,
    val filter: String = "item"
)

@Serializable
private data class Response(
    val format: String = "json-stat2"
)