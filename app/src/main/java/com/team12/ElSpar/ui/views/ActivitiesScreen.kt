package com.team12.ElSpar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team12.ElSpar.R
import java.time.LocalDateTime
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.navigation.NavHostController
import java.math.RoundingMode

private const val SHOWER_KWH_MIN = 0.6 // kWh/min
private const val LAUNDRY_KWH_MIN = 0.008 // kWh/min
private const val OVEN_KWH_MIN = 0.038 // kWh/min
private const val CAR_KWH = 1.0 // kWh

private data class ElectricityActivity(
    val name: String,
    val preference: Int,
    val unit: String,
    val usagePerMin: Double,
    val icon: Int
)

@Composable
fun ActivitiesScreen(
    currentPrice: Map<LocalDateTime, Double>,
    showerPref: Int,
    laundryPref: Int,
    ovenPref: Int,
    carPref: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val price = currentPrice
        .filterKeys { it.hour == LocalDateTime.now().hour }
        .values
        .first()

    val activities = listOf(
        ElectricityActivity("dusj", showerPref, "min", SHOWER_KWH_MIN, R.drawable.showericon),
        ElectricityActivity("klesvask", laundryPref, "min", LAUNDRY_KWH_MIN, R.drawable.laundry),
        ElectricityActivity("ovn", ovenPref, "min", OVEN_KWH_MIN, R.drawable.oven),
        ElectricityActivity("el-bil", carPref, "kWh", CAR_KWH, R.drawable.charger)
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        CurrentPriceCard(currentPrice, navController)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(activities) { activity ->
                ActivityCard(
                    currentPrice = price,
                    usagePerMin = activity.usagePerMin,
                    preference = activity.preference,
                    unit = activity.unit,
                    name = activity.name,
                    icon = activity.icon,
                    navController = navController
                )
            }
        }

        val diff = price/currentPrice.values.average()

        //Bottom text
        var text = stringResource(
            R.string.price_x_above_average,
            diff)
        text += if(diff < 1){
            stringResource(R.string.good_idea)
        } else{
            if (currentPrice.filter {
                    it.key > LocalDateTime.now() && it.value < price }.isEmpty()) {
                stringResource(R.string.no_cheaper_price_today)
            } else stringResource(R.string.bad_idea)
        }

        Spacer(modifier = modifier.size(15.dp))

        //Header
        Text(
            text = "Prisen nå",
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth(0.8f),
            fontWeight = Bold
        )

        Row {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth(0.8f)
            )

            //Icon that shows check when the price is low, and warning when the price is high
            if ((currentPrice.values.average() - price) > 1) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Check icon",
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning icon",
                )
            }
        }
    }
}

@Composable
fun ActivityCard(
    currentPrice: Double,
    preference : Int,
    unit: String,
    usagePerMin: Double,
    name: String,
    icon: Int,
    navController: NavHostController,
    modifier : Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier.clickable { navController.navigate(ElSparScreen.Preference.name) }
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            val activityCost = usagePerMin * (currentPrice/100) * preference

            Image(
                painter = painterResource(id = icon),
                contentDescription = "My Image",
            )
            Text(text = "$preference $unit $name", textAlign = TextAlign.Center)

            //Bottom text-row. Has activity price
            Text(
                buildAnnotatedString {
                    //This string is the price
                    withStyle(style = SpanStyle(fontWeight = Bold, fontSize = 20.sp)) {
                        append(activityCost.toBigDecimal().setScale(1, RoundingMode.CEILING).toString() + "kr ")
                    }
                },
                textAlign = TextAlign.Center
            )
        }
    }
}
