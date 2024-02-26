package com.team12.ElSpar.data

import androidx.datastore.core.DataStore
import com.team12.ElSpar.Settings
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

//DEFAULT VALUES
private const val SHOWER = 10
private const val WASH = 30
private const val OVEN = 15
private const val CAR = 72 //Batterikapasiteten til Norges mest solgte el-bil 2022, Tesla Model Y

interface SettingsRepository {
    val settingsFlow: Flow<Settings>
    val iODispatcher: CoroutineDispatcher
    suspend fun initialStartupCompleted()
    suspend fun updatePriceArea(area: Settings.PriceArea)
    suspend fun updateActivity(activity: Settings.Activity, value: Int)
    suspend fun initializeValues()
}

class DefaultSettingsRepository(
    private val settingsStore: DataStore<Settings>,
    override val iODispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsRepository {
    override val settingsFlow: Flow<Settings> = settingsStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(Settings.getDefaultInstance())
            } else {
                throw exception
            }
        }
    override suspend fun initialStartupCompleted() {
        withContext(iODispatcher){
            settingsStore.updateData { settings ->
                settings.toBuilder().setInitialStartupCompleted(true).build()
            }
        }
    }

    override suspend fun updatePriceArea(area: Settings.PriceArea) {
        withContext(iODispatcher){
            settingsStore.updateData { settings ->
                settings.toBuilder().setArea(area).build()
            }
        }
    }

    override suspend fun updateActivity(activity: Settings.Activity, value: Int) {
        withContext(iODispatcher){
            settingsStore.updateData { settings ->
                when (activity) {
                    Settings.Activity.SHOWER -> settings.toBuilder().setShower(value).build()
                    Settings.Activity.WASH -> settings.toBuilder().setWash(value).build()
                    Settings.Activity.OVEN -> settings.toBuilder().setOven(value).build()
                    Settings.Activity.CAR -> settings.toBuilder().setCar(value).build()
                    Settings.Activity.UNRECOGNIZED -> settings
                }
            }
        }
    }

    override suspend fun initializeValues() {
        withContext(iODispatcher){
            settingsStore.updateData { settings ->
                settings.toBuilder()
                    .setShower(SHOWER)
                    .setWash(WASH)
                    .setOven(OVEN)
                    .setCar(CAR)
                    .build()
            }
        }
    }
}