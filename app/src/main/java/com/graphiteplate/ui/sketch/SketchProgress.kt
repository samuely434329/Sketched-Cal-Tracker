package com.graphiteplate.ui.sketch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.graphiteplate.ui.sketch.SketchStroke.crossHatch
import com.graphiteplate.ui.sketch.SketchStroke.sketchyArc
import com.graphiteplate.ui.sketch.SketchStroke.sketchyRoundedRect
import com.graphiteplate.ui.theme.SketchTheme
import kotlin.math.min

/**
 * Horizontal progress bar drawn as a sketchy capsule. Filled portion is
 * cross-hatched in a darker graphite tone; the unfilled portion shows
 * only the outline.
 *
 * @param progress 0f..1f. Values >1f are clipped but the overshoot is
 *   tinted [SketchTheme.colors.sanguine] to flag overconsumption.
 */
@Composable
fun SketchProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    seedKey: Any? = null,
) {
    val colors = SketchTheme.colors
    val seed = remember(seedKey) { keySeed(seedKey) + 13 }
    val clamped = progress.coerceIn(0f, 1.2f)
    val over = progress > 1f
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        val pad = 3f
        val outerRect = Rect(pad, pad, size.width - pad, size.height - pad)
        val cornerR = (size.height - pad * 2f) / 2f
        // Filled portion.
        val fillEnd = pad + (size.width - pad * 2f) * min(clamped, 1f)
        if (fillEnd > pad + 2f) {
            val fillRect = Rect(pad + 2f, pad + 2f, fillEnd, size.height - pad - 2f)
            crossHatch(
                rect = fillRect,
                color = colors.graphite2B.copy(alpha = 0.85f),
                density = 4f,
                strokeWidth = 1.0f,
                seed = seed + 1,
            )
        }
        // Overshoot (red sanguine) past 100%.
        if (over) {
            val overStart = pad + (size.width - pad * 2f)
            val overEnd = pad + (size.width - pad * 2f) * clamped
            if (overEnd > overStart) {
                val overRect = Rect(overStart, pad + 2f, overEnd, size.height - pad - 2f)
                crossHatch(
                    rect = overRect,
                    color = colors.sanguine.copy(alpha = 0.85f),
                    density = 3.5f,
                    strokeWidth = 1.1f,
                    seed = seed + 23,
                )
            }
        }
        sketchyRoundedRect(
            rect = outerRect,
            color = colors.graphite6B,
            strokeWidth = 1.6f,
            cornerRadius = cornerR,
            seed = seed,
            jitter = 1.0f,
        )
    }
}

/**
 * Circular gauge for the headline daily-calorie total. The arc fills
 * clockwise from 12-o'clock and is drawn as a sketchy polyline. Tick
 * marks every 25% give it a "gauge in a lab notebook" feel.
 */
@Composable
fun SketchProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 6.dp,
    seedKey: Any? = null,
    content: @Composable () -> Unit = {},
) {
    val colors = SketchTheme.colors
    val seed = remember(seedKey) { keySeed(seedKey) + 41 }
    val clamped = progress.coerceIn(0f, 1.2f)
    val over = progress > 1f
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val pad = strokeWidth.toPx() * 2f
            val radius = (this.size.minDimension / 2f) - pad
            // Faint background ring (graphite 2H).
            sketchyArc(
                center = center,
                radius = radius,
                startAngle = -90f,
                sweep = 360f,
                color = colors.graphite2H.copy(alpha = 0.6f),
                strokeWidth = strokeWidth.toPx() * 0.7f,
                seed = seed,
                jitter = 1.4f,
            )
            // Filled arc.
            val fillSweep = 360f * min(clamped, 1f)
            if (fillSweep > 1f) {
                sketchyArc(
                    center = center,
                    radius = radius,
                    startAngle = -90f,
                    sweep = fillSweep,
                    color = colors.graphite8B,
                    strokeWidth = strokeWidth.toPx(),
                    seed = seed + 7,
                    jitter = 1.4f,
                )
            }
            // Overshoot in sanguine red.
            if (over) {
                sketchyArc(
                    center = center,
                    radius = radius,
                    startAngle = -90f + 360f,
                    sweep = 360f * (clamped - 1f),
                    color = colors.sanguine,
                    strokeWidth = strokeWidth.toPx(),
                    seed = seed + 19,
                    jitter = 1.6f,
                )
            }
            // Tick marks at 25/50/75/100%.
            val tickPositions = listOf(0f, 0.25f, 0.5f, 0.75f)
            tickPositions.forEach { frac ->
                val angle = Math.toRadians((-90f + 360f * frac).toDouble())
                val cosA = kotlin.math.cos(angle).toFloat()
                val sinA = kotlin.math.sin(angle).toFloat()
                val tickInner = radius - strokeWidth.toPx() * 1.2f
                val tickOuter = radius + strokeWidth.toPx() * 0.6f
                drawLine(
                    color = colors.graphite6B.copy(alpha = 0.7f),
                    start = Offset(
                        center.x + cosA * tickInner,
                        center.y + sinA * tickInner,
                    ),
                    end = Offset(
                        center.x + cosA * tickOuter,
                        center.y + sinA * tickOuter,
                    ),
                    strokeWidth = 1.4f,
                    cap = StrokeCap.Round,
                )
            }
        }
        content()
    }
}

/** Faint downward triangle marker — used to point at gauge regions. */
@Composable
fun SketchMarkerColor(): Color = SketchTheme.colors.graphite6B
