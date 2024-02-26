package com.team12.ElSpar.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.team12.ElSpar.Settings
import com.team12.ElSpar.ElSparApplication
import com.team12.ElSpar.data.SettingsRepository
import com.team12.ElSpar.domain.GetPowerPriceUseCase
import com.team12.ElSpar.model.PricePeriod
import com.team12.ElSpar.exceptions.NoConnectionException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val MAX_DAYS_AHEAD = 3L
private const val EARLIEST_DATE = "2021-12-01"

class ElSparViewModel(
    private val getPowerPriceUseCase: GetPowerPriceUseCase,
    private val settingsRepository: SettingsRepository,
    //this variable is only set as true when creating the viewModel for tests
    private val isATest: Boolean = false
) : ViewModel() {
    private val _uiState: MutableStateFlow<ElSparUiState> = MutableStateFlow(ElSparUiState.Loading)
    val uiState: StateFlow<ElSparUiState> = _uiState.asStateFlow()

    val settings: Flow<Settings> = settingsRepository.settingsFlow
    //variable only used under testing
    private var viewModelPriceArea: Settings.PriceArea = Settings.PriceArea.NO1

    private var currentPricePeriod = PricePeriod.DAY
    private var currentEndDate = LocalDate.now()

    init {
        viewModelScope.launch {
            if(!isATest){
                settings.collect { settings ->
                    if (!settings.initialStartupCompleted) {
                        settingsRepository.initializeValues()
                        _uiState.value = ElSparUiState.SelectArea(currentPriceArea = settings.area)
                    } else {
                        getPowerPrice()
                    }
                }
            }
            else{
                getPowerPrice()
            }

        }
    }

    fun getPowerPrice() {
        _uiState.value = ElSparUiState.Loading
        viewModelScope.launch {
            if(!isATest) {
                settings.collect { settings ->
                    _uiState.value = try {
                         ElSparUiState.Success(
                            currentPricePeriod = currentPricePeriod,
                            currentEndDate = currentEndDate,
                            priceList = getPowerPriceUseCase(
                                endDate = currentEndDate,
                                period = currentPricePeriod,
                                area = settings.area
                            ),
                            currentPrice = getPowerPriceUseCase(
                                endDate = LocalDate.now(),
                                period = PricePeriod.DAY,
                                area = settings.area
                            )
                        ).also { cache() }
                    } catch (e: NoConnectionException) {
                        ElSparUiState.Error("Kunne ikke laste inn data.\n" +
                                "Vennligst sjekk nettverksforbindelsen din.")
                    }
                }
            }else{
                _uiState.value = try {
                    ElSparUiState.Success(
                        currentPricePeriod = currentPricePeriod,
                        currentEndDate = currentEndDate,
                        priceList = getPowerPriceUseCase(
                            endDate = currentEndDate,
                            period = currentPricePeriod,
                            area = viewModelPriceArea
                        ),
                        currentPrice = getPowerPriceUseCase(
                            endDate = LocalDate.now(),
                            period = PricePeriod.DAY,
                            area = viewModelPriceArea
                        )
                    )
                } catch (e: NoConnectionException) {
                    ElSparUiState.Error("Kunne ikke laste inn data.\n" +
                            "Vennligst sjekk nettverksforbindelsen din.")
                }
            }
        }
    }


    private fun cache(
        buffer: PricePeriod = PricePeriod.MONTH
    ) {
        viewModelScope.launch {
            if(!isATest){
                settings.collect { settings ->
                    getPowerPriceUseCase(
                        endDate = LocalDate.now(),
                        period = buffer,
                        area = settings.area
                    )
                }
            }
            else{
                getPowerPriceUseCase(
                    endDate = LocalDate.now(),
                    period = buffer,
                    area = viewModelPriceArea
                )
            }
        }
    }

    fun updatePricePeriod(pricePeriod: PricePeriod) {
        currentPricePeriod = pricePeriod
        getPowerPrice()
    }

    fun dateForward() {
        val targetDate = currentEndDate.plusDays(currentPricePeriod.days.toLong())
        if (targetDate < LocalDate.now().plusDays(MAX_DAYS_AHEAD)) {
            currentEndDate = targetDate
            getPowerPrice()
        }
    }

    fun dateBack() {
        if (currentEndDate.minusDays(2*currentPricePeriod.days.toLong())
            > LocalDate.parse(EARLIEST_DATE)) {
            currentEndDate = currentEndDate.minusDays(currentPricePeriod.days.toLong())
            getPowerPrice()
        }
    }

    fun updatePreference(priceArea: Settings.PriceArea) {
        viewModelScope.launch {
            if(!isATest){
                currentEndDate = LocalDate.now()
                settingsRepository.updatePriceArea(priceArea)
                settingsRepository.initialStartupCompleted()
            }else{
                viewModelPriceArea = priceArea
            }
            getPowerPrice()
        }
    }

    fun updatePreference(activity: Settings.Activity, value: Int) {
        viewModelScope.launch {
            settingsRepository.updateActivity(activity, value)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                (this[APPLICATION_KEY] as ElSparApplication).container.run {
                    ElSparViewModel(
                        getPowerPriceUseCase = getPowerPriceUseCase,
                        settingsRepository = settingsRepository
                    )
                }
            }
        }
    }
}