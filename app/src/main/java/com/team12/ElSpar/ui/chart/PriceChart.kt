package com.team12.ElSpar.ui.chart

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DecimalFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent

import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.extension.sumOf
import com.patrykandpatrick.vico.core.extension.transformToSpannable
import com.patrykandpatrick.vico.core.marker.Marker
import com.team12.ElSpar.model.PricePeriod
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime

@Composable
fun PriceChart(
    priceList: Map<LocalDateTime, Double>,
    pricePeriod: PricePeriod,
    modifier: Modifier = Modifier,
    pattern: DecimalFormat = DecimalFormat("#")
        .apply { roundingMode = RoundingMode.CEILING },
    startAxisValueFormatter: AxisValueFormatter<AxisPosition.Vertical.Start> =
        DecimalFormatAxisValueFormatter(pattern.toPattern(), RoundingMode.UP),
    bottomAxisValueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom> =
        AxisValueFormatter { value, chartValues ->
            (chartValues.chartEntryModel.entries.first().getOrNull(value.toInt()) as? PriceEntry)
                ?.localDate
                ?.run { if (pricePeriod == PricePeriod.DAY) "$hour" else "$dayOfMonth.$monthValue" }
                .orEmpty()
        },
    marker: Marker = priceMarker { markedEntries ->
        markedEntries.transformToSpannable(
            prefix = if (markedEntries.size > 1) pattern.format(markedEntries.sumOf { it.entry.y }) + " (" else "",
            postfix = if (markedEntries.size > 1) ")" else "",
            separator = "; "
        ) { model ->
            appendCompat(
                if (model.entry is AveragePriceEntry) {
                    (model.entry as AveragePriceEntry).run {
                        "Gjennomsnitt: ${pattern.format(y)}\n" +
                                "Høyeste: ${pattern.format(maxPrice)}\n" +
                                "Laveste: ${pattern.format(minPrice)}"
                    }
                }
                else pattern.format(model.entry.y),
                ForegroundColorSpan(model.color),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    },

    chartStyle: ChartStyle = m3ChartStyle()
) {


    val colorArray = if (!isSystemInDarkTheme()) arrayOf(
        Color(0xFFB36D6D).copy(0.6f),
        Color.Yellow.copy(0.5f),
        Color(0xFF597d4f).copy(0.4f)
    )
    else
        arrayOf(
            Color(0x998c1d18),
            Color(0x994a4458),
            Color(0x994f378b)
        )
    ProvideChartStyle(chartStyle) {
        Chart(
            chart = lineChart(
                lines = listOf(
                    lineSpec(
                        //lineColor = MaterialTheme.colorScheme.primary,
                        lineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        lineThickness = 1.5.dp,
                        lineBackgroundShader = verticalGradient(
                            colors = colorArray,
                            positions = floatArrayOf(0.1f, 0.9f, 1f)
                        )
                    ),
                ).apply {
                    first().pointConnector = PriceChartPointConnector()
                },
                spacing = 0.dp,
                //axisValuesOverrider = AxisValuesOverrider.adaptiveYValues(1.2f),
            ),
            modifier = modifier,
            model = model(priceList, pricePeriod),
            startAxis = startAxis(
                valueFormatter = startAxisValueFormatter,

                label = axisLabelComponent(
                    background = ShapeComponent(
                        shape = Shapes.roundedCornerShape(30,30,30,30,),
                        //color = PurpleGrey80.copy(0.8f).toArgb())
                        color = Color.Black.copy(alpha = 0.0f).toArgb())

                ),
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                titleComponent = textComponent(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    background = shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.primaryContainer),
                    padding = dimensionsOf(20.dp, 1.dp),
                    margins = dimensionsOf(1.dp),
                    typeface = Typeface.DEFAULT,
                    textSize = 15.sp,
                ),
                title = "øre/kWh",
            ),
            bottomAxis = bottomAxis(
                tickLength = 4.dp,
                tickPosition = HorizontalAxis.TickPosition.Center(
                    offset = 0,
                    spacing = if (pricePeriod == PricePeriod.WEEK) 1 else 3
                ),
                valueFormatter = bottomAxisValueFormatter
            ),
            marker = marker,
        )
        chartStyle.lineChart.lines.first().pointConnector = PriceChartPointConnector()
    }
}

private fun model(
    priceList: Map<LocalDateTime, Double>,
    pricePeriod: PricePeriod
): ChartEntryModel =
    if (pricePeriod == PricePeriod.DAY) {
        priceList.entries
            .mapIndexed { index, (hour, y) ->
                PriceEntry(hour, index.toFloat(), y.toFloat())
            }
            .let { ChartEntryModelProducer(it).getModel() }
    } else {
        priceList.entries
            .groupBy(
                { it.key.toLocalDate() },
                { it.value }
            )
            .map {
                it.key to Triple(
                    it.value.max().toFloat(),
                    it.value.min().toFloat(),
                    it.value.average().toFloat()
                )
            }
            .mapIndexed { index, (date, prices) ->
                AveragePriceEntry(
                    prices.first,
                    prices.second,
                    date.atStartOfDay(),
                    index.toFloat(),
                    prices.third)
            }
            .let { ChartEntryModelProducer(it).getModel() }
    }