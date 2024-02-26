package com.team12.ElSpar.ui.chart

import android.graphics.Path
import android.graphics.RectF
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.segment.SegmentProperties

class PriceChartPointConnector : LineChart.LineSpec.PointConnector {
    override fun connect(
        path: Path,
        prevX: Float,
        prevY: Float,
        x: Float,
        y: Float,
        segmentProperties: SegmentProperties,
        bounds: RectF,
    ) {
        path.lineTo(x, prevY)
        path.lineTo(x, y)
    }
}