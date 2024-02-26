package com.team12.ElSpar.ui.chart

import com.patrykandpatrick.vico.core.entry.ChartEntry
import java.time.LocalDateTime

open class PriceEntry(
    open val localDate: LocalDateTime,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = PriceEntry(localDate, x, y)
}

class AveragePriceEntry(
    val maxPrice: Float,
    val minPrice: Float,
    override val localDate: LocalDateTime,
    override val x: Float,
    override val y: Float
) : PriceEntry(localDate, x, y) {
    override fun withY(y: Float) =
        AveragePriceEntry(maxPrice, minPrice, localDate, x, y)
}