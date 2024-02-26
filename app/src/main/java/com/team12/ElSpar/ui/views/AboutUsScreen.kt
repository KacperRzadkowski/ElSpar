package com.team12.ElSpar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.team12.ElSpar.R

//About us composable.
@Composable
fun AboutUsScreen(modifier:Modifier = Modifier){
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ){
        //Card that holds all the text on this page
        Card(
            modifier = Modifier.padding(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ){
            Text(
                text = stringResource(R.string.Gruppen),
                modifier = Modifier.padding(5.dp),
                fontWeight = Bold
            )
            Text(
                text = "ElSpar har blitt utviklet av en gruppe på 6 informatikkstudenter fra UIO i kurset IN2000 - Software Engineering gjennom et prosjektarbeid. Appen er utviklet våren 2023\n\n",
                modifier = Modifier.padding(5.dp)
            )

            Text(
                text = stringResource(R.string.AppMaal),
                modifier = Modifier.padding(5.dp),
                fontWeight = Bold
            )
            Text(
                text =  "Målet vårt med denne appen, bortsett fra at vi må lage en app for " +
                        "å få en god karakter, var å opplyse folk  om strømpriser. Dessuten tror vi " +
                        "at denne appen kan få brukerene " +
                        "til å spare en del penger ved å endre rutinene deres. \n\n",
                modifier = Modifier.padding(5.dp)
                )

            Text(
                text = "Hvorfor vi ville lage appen",
                modifier = Modifier.padding(5.dp),
                fontWeight = Bold
            )
            Text(
                text =  "Vi mener at strømpriser er viktige ettersom vi selv sliter " +
                        "med å betale strømrekningen, og har snakket med flere som sliter med det samme problemet. " +
                        "Vi kom fram til at den beste måten vi kan ha innvirkning på dette er ved å utvikle en app "+
                        "som hjelper folk med å redusere sine strømutgifter ved å endre sine vaner.",
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}