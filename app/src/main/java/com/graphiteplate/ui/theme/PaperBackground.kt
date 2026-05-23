package com.graphiteplate.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random

/**
 * Layer that simulates paper grain procedurally. Avoids bundling a large
 * raster asset, but the look is still that of warm cream stock with
 * faint fiber and light vignetting at the edges.
 *
 * The texture is generated once per composition and cached, since
 * regenerating each frame would jitter the grain visibly.
 */
@Composable
fun PaperBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = SketchTheme.colors
    // Pre-compute a stable list of fiber positions so the grain doesn't
    // animate. Using remember(Unit) anchors the seed to the lifetime of
    // the composable.
    val grainSeed = remember { Random(0xCAFE) }
    val fibers = remember {
        List(900) {
            FiberSpec(
                xFrac = grainSeed.nextFloat(),
                yFrac = grainSeed.nextFloat(),
                len = grainSeed.nextFloat() * 4f + 1f,
                angle = grainSeed.nextFloat() * Math.PI.toFloat() * 2f,
                alpha = grainSeed.nextFloat() * 0.08f + 0.02f,
            )
        }
    }
    val flecks = remember {
        List(120) {
            FleckSpec(
                xFrac = grainSeed.nextFloat(),
                yFrac = grainSeed.nextFloat(),
                radius = grainSeed.nextFloat() * 1.4f + 0.4f,
                alpha = grainSeed.nextFloat() * 0.18f + 0.05f,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Base paper fill.
                drawRect(colors.paper)

                // Soft vignette: paint a darker rectangle and rely on
                // multiple low-alpha passes to avoid hard banding.
                val vignetteColor = colors.paperShadow.copy(alpha = 0.18f)
                drawRect(
                    color = vignetteColor,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = size,
                    style = Stroke(width = 64f),
                )

                // Fibers: short randomly-oriented strokes that imitate
                // the pulp grain in handmade paper.
                fibers.forEach { f ->
                    val cx = f.xFrac * size.width
                    val cy = f.yFrac * size.height
                    val dx = kotlin.math.cos(f.angle) * f.len
                    val dy = kotlin.math.sin(f.angle) * f.len
                    drawLine(
                        color = colors.graphite6H.copy(alpha = f.alpha),
                        start = androidx.compose.ui.geometry.Offset(cx - dx, cy - dy),
                        end = androidx.compose.ui.geometry.Offset(cx + dx, cy + dy),
                        strokeWidth = 0.6f,
                    )
                }

                // Specks: tiny darker spots that read as inclusions.
                flecks.forEach { f ->
                    drawCircle(
                        color = Color(0xFF6B5C46).copy(alpha = f.alpha),
                        radius = f.radius,
                        center = androidx.compose.ui.geometry.Offset(
                            f.xFrac * size.width,
                            f.yFrac * size.height,
                        ),
                    )
                }
            },
    ) {
        content()
    }
}

private data class FiberSpec(
    val xFrac: Float,
    val yFrac: Float,
    val len: Float,
    val angle: Float,
    val alpha: Float,
)

private data class FleckSpec(
    val xFrac: Float,
    val yFrac: Float,
    val radius: Float,
    val alpha: Float,
)
