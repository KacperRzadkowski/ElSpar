package com.team12.ElSpar.fake

import com.team12.ElSpar.api.HvaKosterStrommenApiService
import com.team12.ElSpar.api.MetApiService
import com.team12.ElSpar.data.*
import com.team12.ElSpar.domain.*
import com.team12.ElSpar.AppContainer
import com.team12.ElSpar.api.SsbApiService
import com.team12.ElSpar.ml.Model
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

class FakeAppContainer @OptIn(ExperimentalCoroutinesApi::class) constructor(
    override val settingsRepository: SettingsRepository,
    iODispatcher: TestDispatcher = StandardTestDispatcher(),
    ) : AppContainer{
    //mocked model since the variable is required to implement child class oAppContainer.
    override val model: Model = mockk()

    //APIs
    private val frostApiService:  MetApiService = FakeMetApiService()
    private val locationForecastApiService: MetApiService = FakeMetApiService()
    private val hvaKosterStrommenApiService: HvaKosterStrommenApiService =
        FakeHvaKosterStrommenApiService()
    private val fakeSsbApiService: SsbApiService = FakeSsbApiService()

    //repos
    private val powerRepository: PowerRepository =
        DefaultPowerRepository(hvaKosterStrommenApiService)
    val weatherRepository: WeatherRepository =
        DefaultWeatherRepository(
            frostApiService = frostApiService,
            locationForecastApiService = locationForecastApiService,
        )
    val statisticsRepository: StatisticsRepository =
        SsbStatisticsRepository(
            ssbApiService = fakeSsbApiService
        )

    //Usecase
    override val getPowerPriceUseCase: GetPowerPriceUseCase =
        GetPowerPriceUseCase(
            powerRepository = powerRepository,
            getProjectedPowerPriceUseCase = null,
            iODispatcher = iODispatcher
        )
}
