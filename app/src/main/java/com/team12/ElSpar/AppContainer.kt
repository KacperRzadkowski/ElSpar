package com.team12.ElSpar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.team12.ElSpar.api.*
import com.team12.ElSpar.data.*
import com.team12.ElSpar.domain.*
import com.team12.ElSpar.ml.Model
import com.team12.ElSpar.network.KtorClient

private const val DATA_STORE_FILE_NAME = "settings.pb"

interface AppContainer {
    val model: Model
    val getPowerPriceUseCase: GetPowerPriceUseCase
    val settingsRepository: SettingsRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {
    override val model = Model.newInstance(context)

    private val settingsStore: DataStore<Settings> =
        DataStoreFactory.create(SettingsSerializer) {
            context.dataStoreFile(DATA_STORE_FILE_NAME)
        }

    //API-SERVICES
    private val hvaKosterStrommenApiService: HvaKosterStrommenApiService =
        DefaultHvaKosterStrommenApiService(KtorClient.httpClient)

    private val frostApiService: MetApiService =
        FrostApiService(KtorClient.httpClient)

    private val locationForecastApiService: MetApiService =
        LocationForecastApiService(KtorClient.httpClient)

    private val ssbApiService: SsbApiService =
        DefaultSsbApiService(KtorClient.httpClient)

    //REPOSITORIES
    override val settingsRepository: SettingsRepository =
        DefaultSettingsRepository(settingsStore)

    private val powerRepository: PowerRepository =
        DefaultPowerRepository(hvaKosterStrommenApiService)

    private val weatherRepository: WeatherRepository =
        DefaultWeatherRepository(
            frostApiService = frostApiService,
            locationForecastApiService = locationForecastApiService
        )

    private val statisticsRepository: StatisticsRepository =
        SsbStatisticsRepository(ssbApiService)

    //DOMAIN LAYER USE CASES
    private val getProjectedPowerPriceUseCase: GetProjectedPowerPriceUseCase =
        GetProjectedPowerPriceUseCase(
            powerRepository = powerRepository,
            weatherRepository = weatherRepository,
            statisticsRepository = statisticsRepository,
            model = model
        )

    override val getPowerPriceUseCase: GetPowerPriceUseCase =
        GetPowerPriceUseCase(
            powerRepository = powerRepository,
            getProjectedPowerPriceUseCase = getProjectedPowerPriceUseCase
        )



}