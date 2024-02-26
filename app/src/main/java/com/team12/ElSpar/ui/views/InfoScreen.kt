package com.team12.ElSpar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team12.ElSpar.R

@Composable
fun InfoScreen(
    modifier : Modifier = Modifier
) {
    //List over images and text
    val imageList: List<Int> = listOf(
        R.drawable.power,
        R.drawable.pay,
        R.drawable.shower,
        R.drawable.prognosepic
    )

    val textList: List<Triple<String, String, String>> = listOf(
        Triple(stringResource(R.string.Hva_er_en_KWH), stringResource(R.string.Underoverskrift1), stringResource(R.string.Brodtekst1)),
        Triple(stringResource(R.string.Spotpris), stringResource(R.string.Underoverskrift4), stringResource(R.string.Brodtekst4)),
        Triple(stringResource(R.string.Aktiviteter), stringResource(R.string.Underoverskrift2), stringResource(R.string.Brodtekst2)),
        Triple(stringResource(R.string.Prognose), stringResource(R.string.Underoverskrift3), stringResource(
                    R.string.prognosis_about)
                )
    )

    //Making lazycolumn with the lists
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        items(textList.size) {
            Box {

                //Header
                Image(
                    painter = painterResource(id = imageList[it]),
                    contentDescription = "Background Image",
                    alpha = 0.5f,
                    modifier = modifier
                        .offset(y = (30).dp).clip(RoundedCornerShape(15.dp))
                        .clip(shape = RoundedCornerShape(10.dp))
                )
                Column(modifier = Modifier
                    .padding(60.dp)
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally

                ) {
                    Text(
                        text = textList[it].first,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(0.8f),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = textList[it].second,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(0.8f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                    )
                }
            }

            //Main text
            Card(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
            ) {

                Text(
                    text = textList[it].third,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
