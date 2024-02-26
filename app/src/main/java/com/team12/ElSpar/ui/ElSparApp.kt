package com.team12.ElSpar.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.team12.ElSpar.R
import com.team12.ElSpar.Settings
import com.team12.ElSpar.ui.viewmodel.ElSparUiState
import com.team12.ElSpar.ui.viewmodel.ElSparViewModel
import com.team12.ElSpar.ui.views.ErrorScreen
import com.team12.ElSpar.ui.views.LoadingScreen




enum class ElSparScreen(@StringRes val title: Int){
    Main(title = R.string.power_overview),
    Activities(title = R.string.power_calculator),
    Settings(title = R.string.settings),
    SelectArea(title = R.string.choose_pricearea),
    Preference(title = R.string.preferences),
    Info(title = R.string.more_about_electricity),
    AboutUs(title = R.string.about_us)
}
@Composable
fun ElSparApp(
    elSparViewModel: ElSparViewModel,
    modifier: Modifier = Modifier
) {
    val elSparUiState: ElSparUiState
    by elSparViewModel.uiState.collectAsState()

    val settings: Settings
    by elSparViewModel.settings.collectAsState(Settings.getDefaultInstance())

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ElSparScreen.valueOf(
        backStackEntry?.destination?.route ?: ElSparScreen.Main.name
    )

    // Scaffold that goes "Outside" the whole app
    // The different screens are just functions that is displyed inside this scaffold
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            //Sets topbar, adn controls when the back-button is visible
            if (elSparUiState !is ElSparUiState.SelectArea) {TopBar(currentScreen, false, navigateUp = { navController.navigateUp() })}
            when (currentScreen) {
                ElSparScreen.SelectArea -> TopBar(currentScreen, true, navigateUp = { navController.navigateUp() })
                ElSparScreen.Preference-> TopBar(currentScreen, true, navigateUp = { navController.navigateUp() })
                ElSparScreen.Info -> TopBar(currentScreen, true, navigateUp = { navController.navigateUp() })
                ElSparScreen.AboutUs-> TopBar(currentScreen, true, navigateUp =  { navController.navigateUp() })
                else -> TopBar(currentScreen, false, navigateUp = { navController.navigateUp() } )
            }
        },
        bottomBar = {
            //Sets bottombar when we want it available.
            //It does not appear when user first is promted to select price area
            if (elSparUiState !is ElSparUiState.SelectArea) NavBar(navController)
        }
    ) { padding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            //Navbar that starts on our main screen, and contains all our different screens
            NavHost(navController = navController, startDestination = ElSparScreen.Main.name) {
                composable(ElSparScreen.Activities.name) {
                    //currentScreen = ElSparScreen.Activities
                    DataContent(
                        elSparUiState = elSparUiState,
                        elSparViewModel = elSparViewModel
                    ) {
                        ActivitiesScreen(
                            currentPrice = it.currentPrice,
                            showerPref = settings.shower,
                            laundryPref = settings.wash,
                            ovenPref = settings.oven,
                            carPref = settings.car,
                            navController = navController
                        )
                    }
                }

                composable(ElSparScreen.Main.name) {
                    //currentScreen = ElSparScreen.Main
                    DataContent(
                        elSparUiState = elSparUiState,
                        elSparViewModel = elSparViewModel
                    ) {
                        MainScreen(
                            currentPrice = it.currentPrice,
                            priceList = it.priceList,
                            currentPricePeriod = it.currentPricePeriod,
                            currentEndDate = it.currentEndDate,
                            onChangePricePeriod = { elSparViewModel.updatePricePeriod(it) },
                            onDateForward = { elSparViewModel.dateForward() },
                            onDateBack = { elSparViewModel.dateBack() },
                            modifier = modifier,
                            navController
                        )
                    }
                }

                composable(ElSparScreen.Settings.name) {
                    //currentScreen = ElSparScreen.Settings
                    SettingsScreen(
                        onChangePreferences = {navController.navigate(ElSparScreen.Preference.name)},
                        onChangePrisomraade  = {navController.navigate(ElSparScreen.SelectArea.name)},
                        onChangeInfo  = {navController.navigate(ElSparScreen.Info.name)},
                        onChangeAboutUs  = {navController.navigate(ElSparScreen.AboutUs.name)},
                    )
                }
                composable(ElSparScreen.Preference.name){
                    //currentScreen = ElSparScreen.Preference
                    PreferenceScreen(
                        shower = settings.shower,
                        wash = settings.wash,
                        oven = settings.oven,
                        car = settings.car,
                        onUpdatedPreference = { activity, value ->
                            elSparViewModel.updatePreference(activity, value)
                        }
                    )
                }
                composable(ElSparScreen.SelectArea.name){
                    //currentScreen = ElSparScreen.SelectArea
                    SelectAreaScreen(
                        currentPriceArea = settings.area,
                        onChangePriceArea = { elSparViewModel.updatePreference(it) }
                    )
                }
                composable(ElSparScreen.Info.name){
                    //currentScreen = ElSparScreen.Info
                    InfoScreen()
                }
                composable(ElSparScreen.AboutUs.name){
                    //currentScreen = ElSparScreen.AboutUs
                    AboutUsScreen()
                }
            }
        }
    }
}

@Composable
//Function for composables that need data loaded in by a blocking function
fun DataContent(
    elSparUiState: ElSparUiState,
    elSparViewModel: ElSparViewModel,
    modifier: Modifier = Modifier,
    onSuccessfulLoadContent: @Composable (ElSparUiState.Success) -> Unit
) {
    //Sets screen based on the state
    when (elSparUiState) {
        is ElSparUiState.SelectArea -> SelectAreaScreen(
            currentPriceArea = elSparUiState.currentPriceArea,
            onChangePriceArea = { elSparViewModel.updatePreference(it) }
        )
        is ElSparUiState.Loading -> LoadingScreen(modifier)
        is ElSparUiState.Error -> ErrorScreen(
            error = elSparUiState.error,
            retryAction = { elSparViewModel.getPowerPrice() }
        )
        is ElSparUiState.Success -> onSuccessfulLoadContent(elSparUiState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//The topbar displayed in the scaffold
fun TopBar(
    currScreen: ElSparScreen,
    button: Boolean,
    navigateUp: () -> Unit
){
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
                //Disable the back button based on the bool
                enabled = button,
                colors = IconButtonDefaults.iconButtonColors(
                    //Set disabled color to transparent
                    disabledContentColor = Color.Black.copy(alpha = 0.0f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "ArrowBack Icon"
                )
            }
        },
        title = { Text(text = stringResource(currScreen.title), color = MaterialTheme.colorScheme.onPrimaryContainer) },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
//Navbar displayed in scaffold
fun NavBar(navController: NavHostController){
    BottomAppBar(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        //Row with navigation icon-buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ){
            IconButton(onClick = { navController.navigate(ElSparScreen.Activities.name)}) {
                Icon(
                    painter = painterResource(id = R.drawable.calculatesmall),
                    contentDescription = "Calculate Icon"
                )
            }
            IconButton(onClick = { navController.navigate(ElSparScreen.Main.name)}) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Icon"
                )
            }
            IconButton(onClick = { navController.navigate(ElSparScreen.Settings.name)}) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "List Icon"
                )
            }
        }
    }
}