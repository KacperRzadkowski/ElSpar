package com.team12.ElSpar

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.team12.ElSpar.data.DefaultSettingsRepository
import com.team12.ElSpar.data.SettingsSerializer
import com.team12.ElSpar.fake.*
import com.team12.ElSpar.model.PricePeriod
import com.team12.ElSpar.rules.MainDispatcherRule
import com.team12.ElSpar.ui.viewmodel.ElSparUiState
import com.team12.ElSpar.ui.viewmodel.ElSparViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.LocalDate
import org.junit.Rule


//Checking if uiState updates to Success with correct data after calling getPowerPrice from ViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    lateinit var appContainer: FakeAppContainer
    lateinit var elSparViewModel: ElSparViewModel

    @Before
    fun setUp() {
        val settingsStore: DataStore<Settings> = DataStoreFactory.create(SettingsSerializer) {
            File("fake_test_file")
        }
        val settingsRepository = DefaultSettingsRepository(settingsStore)
        appContainer = FakeAppContainer(settingsRepository = settingsRepository)

        elSparViewModel = ElSparViewModel(
            appContainer.getPowerPriceUseCase,
            appContainer.settingsRepository,
            isATest = true
        )
    }

    //Checking if uiState updates to Success with correct data after calling getPowerPrice from ViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun elSparViewModel_getPowerPrice_verifyElSparUiStateSuccess() =
        runTest{

            elSparViewModel.getPowerPrice()
            advanceUntilIdle()
            assertEquals(
                ElSparUiState.Success(
                    PricePeriod.DAY,
                    LocalDate.now(),
                    FakePowerDataSource.priceDataMapMVA,
                    FakePowerDataSource.priceDataMapMVA
                ),
                elSparViewModel.uiState.value
            )
        }

    //Checking if viewmodels priceArea updates after calling update price area
    @Test
    fun elSparViewModel_updatePricePeriod_verifyElSparViewModelCurrentPricePeriod() =
        runTest {
            elSparViewModel.updatePricePeriod(PricePeriod.WEEK)

            advanceUntilIdle()
            assertEquals(
                ElSparUiState.Success(
                    PricePeriod.WEEK,
                    LocalDate.now(),
                    FakePowerDataSource.priceDataMapMVA,
                    FakePowerDataSource.priceDataMapMVA
                ),
                elSparViewModel.uiState.value
            )
        }
    @Test
    fun elSparViewModel_updatePreference_priceArea_verifyChange() =
        runTest {
            elSparViewModel.updatePreference(Settings.PriceArea.NO2)
            advanceUntilIdle()
            assertEquals(
                ElSparUiState.Success(
                    PricePeriod.DAY,
                    LocalDate.now(),
                    FakePowerDataSource.priceDataMapMVA,
                    FakePowerDataSource.priceDataMapMVA
                ),
                elSparViewModel.uiState.value
            )
    }
    @Test
    fun elSparViewModel_dateBack_verifyDate() =
        runTest{
            elSparViewModel.dateBack()
            advanceUntilIdle()
            assertEquals(
                ElSparUiState.Success(
                    PricePeriod.DAY,
                    LocalDate.now().minusDays(1),
                    FakePowerDataSource.priceDataMapMVA,
                    FakePowerDataSource.priceDataMapMVA
                ),
                elSparViewModel.uiState.value
            )
        }
}
