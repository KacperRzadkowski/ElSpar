package com.team12.ElSpar.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team12.ElSpar.R

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//Composable for showing settings
fun SettingsScreen(
    onChangePreferences :  () -> Unit,
    onChangePrisomraade :  () -> Unit,
    onChangeInfo :  () -> Unit,
    onChangeAboutUs :  () -> Unit,
    modifier : Modifier = Modifier,
){
    val settingCardsTitles  = listOf(
        stringResource(R.string.Preferanser),
        stringResource(R.string.velg_prisområde),
        stringResource(R.string.mer_om_strom),
        stringResource(R.string.om_oss))

    val settingCardsIcons: List<Int>  = listOf(
        R.drawable.tuneicon,
        R.drawable.mapicon2,
        R.drawable.bolticon,
        R.drawable.infoicon
    )

    //LazyColumn with the different settings-buttons
    LazyColumn(
    ) {
        items(settingCardsTitles.size) { index ->
            var onChangeFunction  : () -> Unit = {}
            when (settingCardsTitles[index]){
                "Preferanser" -> onChangeFunction = onChangePreferences
                "Velg prisområde" -> onChangeFunction = onChangePrisomraade
                "Mer om strøm" -> onChangeFunction = onChangeInfo
                "Om oss" -> onChangeFunction = onChangeAboutUs
            }
            Card(
                shape = MaterialTheme.shapes.medium,
                modifier = modifier
                    .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .clickable(onClick = { onChangeFunction() }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
            ) {
                Row(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {

                    //Settings icon
                    Image(
                        painter = painterResource(id = settingCardsIcons[index]),
                        contentDescription = "My Image"
                    )
                    Text(
                        text = settingCardsTitles[index],
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

