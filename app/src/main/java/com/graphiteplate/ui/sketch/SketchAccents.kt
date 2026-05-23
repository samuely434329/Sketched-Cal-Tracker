package com.graphiteplate.ui.sketch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.graphiteplate.ui.sketch.SketchStroke.sketchyLine
import com.graphiteplate.ui.sketch.SketchStroke.sketchyRoundedRect
import com.graphiteplate.ui.theme.SketchTheme

/** Thin sketchy divider line. */
@Composable
fun SketchDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    seedKey: Any? = null,
) {
    val colors = SketchTheme.colors
    val seed = remember(seedKey) { keySeed(seedKey) + 71 }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness * 4),
    ) {
        sketchyLine(
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            color = colors.graphite2H,
            strokeWidth = 1.2f,
            seed = seed,
            jitter = 0.9f,
            passes = 1,
        )
    }
}

/**
 * Section heading: serif title + a sketchy double-underline. Mimics how
 * an engineering notebook flags a new section.
 */
@Composable
fun SketchSectionHeading(
    text: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    val colors = SketchTheme.colors
    val seed = remember(text) { keySeed(text) + 89 }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = colors.graphite8B,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                trailing()
            }
        }
        Spacer(Modifier.height(4.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        ) {
            sketchyLine(
                start = Offset(0f, 2f),
                end = Offset(size.width * 0.55f, 2f),
                color = colors.graphite8B,
                strokeWidth = 1.6f,
                seed = seed,
                passes = 2,
            )
            sketchyLine(
                start = Offset(0f, 7f),
                end = Offset(size.width * 0.40f, 7f),
                color = colors.graphite6B,
                strokeWidth = 1.0f,
                seed = seed + 1,
                passes = 1,
            )
        }
    }
}

/** Pill that labels meal categories ("Breakfast", "Lunch", etc.). */
@Composable
fun SketchTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = SketchTheme.colors
    val seed = remember(text) { keySeed(text) + 109 }
    Box(
        modifier = modifier
            .height(22.dp)
            .drawBehind {
                val pad = 1.5f
                val rect = Rect(pad, pad, size.width - pad, size.height - pad)
                sketchyRoundedRect(
                    rect = rect,
                    color = colors.graphite6B,
                    strokeWidth = 1.2f,
                    cornerRadius = rect.height / 2f,
                    seed = seed,
                    jitter = 0.9f,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
            color = colors.graphite8B,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
    }
}
