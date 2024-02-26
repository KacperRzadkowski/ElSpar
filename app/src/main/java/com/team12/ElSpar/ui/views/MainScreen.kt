package com.team12.ElSpar.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.team12.ElSpar.model.PricePeriod
import com.team12.ElSpar.ui.chart.PriceChart
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@Composable
fun MainScreen(
    currentPrice: Map<LocalDateTime, Double>,
    priceList: Map<LocalDateTime, Double>,
    currentPricePeriod: PricePeriod,
    currentEndDate: LocalDate,
    onChangePricePeriod: (PricePeriod) -> Unit,
    onDateForward: () -> Unit,
    onDateBack: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    Column(
        modifier = modifier
            .fillMaxHeight().verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){

        //Screen content

        //Card with "Current price" on top
        CurrentPriceCard(currentPrice, navController)

        Spacer(modifier = Modifier.size(15.dp))

        //Time interval buttons
        TimeIntervalButtons(currentPricePeriod) { onChangePricePeriod(it) }
        //Date selection buttons
        DateSelectionButtons(currentPricePeriod, currentEndDate, onDateBack, onDateForward)

        //Graph
        PriceChart(priceList, currentPricePeriod)

        Spacer(modifier = Modifier.size(15.dp))

        //Pricetext bottom
        PriceText(priceList, currentPricePeriod)

    }
}

//Card for current price. Is reused in activity-screen
@Composable
fun CurrentPriceCard(
    currentPrice: Map<LocalDateTime, Double>,
    navController: NavHostController
){
    //Current price
    val currPrice = currentPrice
        .filterKeys { it.hour == LocalDateTime.now().hour }
        .values
        .first()

    //Making card clickable
    Card(
        Modifier.clickable { navController.navigate(ElSparScreen.Info.name) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ){

        //Column with the card-elements
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-10).dp)
        ){
            Row(horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information icon",
                )
                Text(text = "Strømpris nå")
            }

            Text(text = roundOffDecimal(currPrice).toString(), fontSize = 50.sp, fontWeight = FontWeight.Bold)
            Text(text = "øre/kWh")
        }
    }
}

//Custom made segmented button
@Composable
fun TimeIntervalButtons(
    currentPricePeriod: PricePeriod,
    onSelectPricePeriod: (PricePeriod) -> Unit
){
    //Row with 3 interval buttons
    Row(modifier = Modifier
        .height(40.dp)
        .fillMaxWidth()
    ){
        IntervalButton(Modifier.weight(1f),40, 0, currentPricePeriod, PricePeriod.DAY) { onSelectPricePeriod(it) }
        IntervalButton(Modifier.weight(1f),0, 0, currentPricePeriod, PricePeriod.WEEK) { onSelectPricePeriod(it) }
        IntervalButton(Modifier.weight(1f),0, 40, currentPricePeriod, PricePeriod.MONTH) { onSelectPricePeriod(it) }
    }
}


@Composable
fun IntervalButton(
    modifier: Modifier,
    leftRound:Int = 0,
    rightRound:Int = 0,
    currentPricePeriod: PricePeriod,
    btnPricePeriod: PricePeriod,
    onSelectPricePeriod: (PricePeriod) -> Unit)
{
    //Selected button gets primary container color, the other gets BG color
    val buttonColor =   if (currentPricePeriod == btnPricePeriod) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.background
    //Setting textcolor to onPrimaryContainer or onBG
    val textColor =     if (currentPricePeriod == btnPricePeriod)MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onBackground

    OutlinedButton(
        onClick = {onSelectPricePeriod(btnPricePeriod)},
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(
            topStartPercent = leftRound,
            topEndPercent = rightRound,
            bottomEndPercent = rightRound,
            bottomStartPercent = leftRound
        )
    ) {
        Text(text = btnPricePeriod.text, color = textColor)
    }
}

//Simple buttons and text for changing day/week/month
@Composable
fun DateSelectionButtons(
    currentPricePeriod: PricePeriod,
    currentEndDate: LocalDate,
    onDateBack: () -> Unit,
    onDateForward: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        //Back button
        OutlinedIconButton(
            onClick = { onDateBack() }
        ) { Icon(
            Icons.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        ) }

        //Date
        Text(
            text = if (currentPricePeriod == PricePeriod.DAY) {
                "${currentEndDate.dayOfMonth}. ${currentEndDate.month.getDisplayName(TextStyle.FULL, Locale("nb"))}"
            } else {
                currentEndDate.minusDays(currentPricePeriod.days-1L).run {
                    "$dayOfMonth. ${month.getDisplayName(TextStyle.FULL, Locale("nb")).toString().take(3)} - " +
                            "${currentEndDate.dayOfMonth}. ${currentEndDate.month.getDisplayName(
                                TextStyle.FULL, Locale("nb")
                            ).toString().take(3)}"
                }
            },
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        )

        //Forward button
        OutlinedIconButton(
            onClick = { onDateForward() }
        ) { Icon(
            Icons.Filled.ArrowForward,
            contentDescription = "Forward",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        ) }

    }
}

//Avg, max and min powerprice
@Composable
fun PriceText(
    priceList: Map<LocalDateTime, Double>,
    pricePeriod: PricePeriod,
) {

    val avgPrice = priceList.values.average()
    val minPrice = priceList.values.min()
    val maxPrice = priceList.values.max()

    val timeOf: (Double) -> String =
    { price ->
        priceList
            .filterValues { it == price }
            .keys
            .first()
            .run {
                if (pricePeriod == PricePeriod.DAY) "kl $hour - ${hour+1}"
                else "$dayOfMonth.$monthValue kl $hour - ${hour+1}"
            }
    }

    //Min price
    val rowMod:Modifier = Modifier.fillMaxWidth()
    Row(rowMod, horizontalArrangement = Arrangement.SpaceBetween){
        Text("Laveste: ${timeOf(minPrice)}")
        Text(roundOffDecimal(minPrice).toString() + " øre/kWh")
    }

    Divider(modifier = Modifier.fillMaxWidth(0.9f).width(1.dp))
    //Max price
    Row(rowMod, horizontalArrangement = Arrangement.SpaceBetween){
        Text("Høyeste: ${timeOf(maxPrice)}")
        Text(roundOffDecimal(maxPrice).toString() + " øre/kWh")
    }

    Divider(modifier = Modifier.fillMaxWidth(0.9f).width(1.dp))
    //Acg price
    Row(rowMod, horizontalArrangement = Arrangement.SpaceBetween){
        Text("Gjennomsnittlig pris:")
        Text(roundOffDecimal(avgPrice).toString() + " øre/kWh")
    }
}

//Function to round number
fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}