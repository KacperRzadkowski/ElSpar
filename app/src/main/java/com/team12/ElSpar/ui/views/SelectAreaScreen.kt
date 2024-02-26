package com.team12.ElSpar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team12.ElSpar.Settings.PriceArea
import com.team12.ElSpar.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
//Screen for selecting price area
fun SelectAreaScreen(
    currentPriceArea: PriceArea,
    onChangePriceArea: (PriceArea) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    var outlinedTextValue by remember { mutableStateOf(currentPriceArea.toString()) }
    var placeHolderPadding by remember { mutableStateOf(0) }
    val maxPlaceHolderPadding = 7

    Column(
        modifier = modifier.fillMaxSize().padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            //Textfield for area
            OutlinedTextField(
                readOnly = true,
                value = outlinedTextValue,
                enabled = false,
                onValueChange = {},
                modifier = modifier.fillMaxWidth(0.9f),
                label = {
                    Text(
                        text = stringResource(R.string.pick_price_area),
                        modifier = modifier.padding(top = placeHolderPadding.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer, //hide the indicator
                    unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground
                ),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(R.string.pick_price_area),
                        )
                    }
                }
            )

            //Dropdown that shows list of priceareas
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = modifier.fillMaxWidth(0.9f)
            ) {
                PriceArea.values().dropLast(1).forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.name) },
                        onClick = {
                            expanded = false
                            placeHolderPadding = maxPlaceHolderPadding
                            onChangePriceArea(it)
                            outlinedTextValue = it.toString()
                        }
                    )
                }
            }
        }

        ImageOfAreas(modifier = Modifier
            .clickable { expanded = true }
            .fillMaxSize()
        )
    }
}

//Image that shows pricearea. Different in dark and lightmode
@Composable
fun ImageOfAreas(modifier: Modifier) {
    val painter =
        if (!isSystemInDarkTheme())
            painterResource(id = R.drawable.prisomrader)
        else
            painterResource(id = R.drawable.prisomraderdark)

    Image(
        painter = painter,
        contentDescription = stringResource(id = R.string.prisomraderPlaceholder),
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}