package com.graphiteplate.ui.sketch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.graphiteplate.ui.sketch.SketchStroke.crossHatch
import com.graphiteplate.ui.sketch.SketchStroke.sketchyRoundedRect
import com.graphiteplate.ui.theme.SketchTheme

enum class SketchButtonStyle { Primary, Secondary, Ghost }

/**
 * Tappable button with a hand-drawn outline. While pressed, the inside
 * fills with a denser cross-hatch to give physical "ink absorption"
 * feedback rather than a digital opacity flash.
 */
@Composable
fun SketchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    style: SketchButtonStyle = SketchButtonStyle.Primary,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    cornerRadius: Dp = 12.dp,
) {
    val colors = SketchTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val seed = remember(text) { keySeed(text) }

    val border = when (style) {
        SketchButtonStyle.Primary -> colors.graphite8B
        SketchButtonStyle.Secondary -> colors.graphite2B
        SketchButtonStyle.Ghost -> colors.graphiteHB
    }
    val ink = when (style) {
        SketchButtonStyle.Primary -> colors.graphite8B
        SketchButtonStyle.Secondary -> colors.graphite6B
        SketchButtonStyle.Ghost -> colors.graphite2B
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .drawBehind {
                val pad = 4f
                val rect = Rect(pad, pad, size.width - pad, size.height - pad)
                // Always lay a faint resting hatch for Primary so the
                // button has visual weight even when not pressed.
                if (style == SketchButtonStyle.Primary) {
                    crossHatch(
                        rect = rect,
                        color = colors.graphite6B.copy(alpha = if (pressed) 0.55f else 0.30f),
                        density = if (pressed) 4f else 6f,
                        strokeWidth = 0.9f,
                        seed = seed + 3,
                        secondPass = pressed,
                    )
                } else if (pressed) {
                    crossHatch(
                        rect = rect,
                        color = colors.graphite2B.copy(alpha = 0.30f),
                        density = 5f,
                        strokeWidth = 0.8f,
                        seed = seed + 5,
                        secondPass = false,
                    )
                }
                sketchyRoundedRect(
                    rect = rect,
                    color = if (enabled) border else colors.graphite2H,
                    strokeWidth = if (style == SketchButtonStyle.Primary) 2.2f else 1.6f,
                    cornerRadius = cornerRadius.toPx(),
                    seed = seed,
                    jitter = 1.3f,
                )
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        val textColor: Color = when (style) {
            SketchButtonStyle.Primary -> colors.paper
            else -> ink
        }
        CompositionLocalProvider(LocalContentColor provides textColor) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )
        }
    }
}
