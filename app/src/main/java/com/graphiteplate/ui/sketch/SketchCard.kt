package com.graphiteplate.ui.sketch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.graphiteplate.ui.sketch.SketchStroke.crossHatch
import com.graphiteplate.ui.sketch.SketchStroke.sketchyRoundedRect
import com.graphiteplate.ui.sketch.SketchStroke.smudge
import com.graphiteplate.ui.theme.SketchTheme

/**
 * Card-like container with a hand-drawn rounded border. Optionally adds
 * a faint cross-hatch wash inside for visual weight.
 *
 * @param key Stable key controlling the deterministic seed. Pass the
 *   logical identity of what the card represents (e.g., a date string)
 *   so the same card looks identical across recompositions.
 * @param emphasized When true, thickens strokes and adds a subtle
 *   drop-shadow smudge to draw the eye.
 */
@Composable
fun SketchCard(
    modifier: Modifier = Modifier,
    key: Any? = null,
    emphasized: Boolean = false,
    shaded: Boolean = false,
    cornerRadius: Dp = 14.dp,
    contentPadding: Dp = 16.dp,
    borderColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val colors = SketchTheme.colors
    val stroke = borderColor ?: if (emphasized) colors.graphite6B else colors.graphite2B
    Box(
        modifier = modifier
            .drawBehind {
                val seed = keySeed(key) + canvasSeed(extra = if (emphasized) 7 else 0)
                val pad = 4f
                val rect = Rect(
                    left = pad,
                    top = pad,
                    right = size.width - pad,
                    bottom = size.height - pad,
                )
                if (shaded) {
                    crossHatch(
                        rect = rect,
                        color = colors.graphite6H.copy(alpha = 0.45f),
                        density = 7f,
                        strokeWidth = 0.6f,
                        seed = seed + 11,
                        secondPass = false,
                    )
                }
                if (emphasized) {
                    smudge(
                        center = Offset(size.width / 2f, size.height + 4f),
                        radius = size.width / 2.4f,
                        color = colors.graphite8B.copy(alpha = 0.25f),
                        seed = seed + 17,
                    )
                }
                sketchyRoundedRect(
                    rect = rect,
                    color = stroke,
                    strokeWidth = if (emphasized) 2.2f else 1.6f,
                    cornerRadius = cornerRadius.toPx(),
                    seed = seed,
                    jitter = 1.4f,
                )
            }
            .padding(contentPadding),
    ) {
        content()
    }
}
