package com.graphiteplate.ui.sketch

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/**
 * Drawing primitives that approximate a 2B pencil on cream paper.
 *
 * A real pencil stroke is not a single line. It is several overlapping
 * passes, each slightly displaced, with varying pressure (alpha) and
 * occasional skips where the graphite runs out. These helpers compose
 * those passes deterministically: every component supplies a [seed] so
 * that a card outlined with seed=42 looks identical on every recompose,
 * which keeps the UI from "wiggling" between frames.
 */
object SketchStroke {

    /**
     * Draw a slightly imperfect line from [start] to [end].
     *
     * Three passes are overlaid: a faint underlayer, the main stroke,
     * and a light topcoat. Each pass is broken into short segments with
     * orthogonal jitter so the result looks hand-drawn rather than
     * mechanically dashed.
     */
    fun DrawScope.sketchyLine(
        start: Offset,
        end: Offset,
        color: Color,
        strokeWidth: Float,
        seed: Int,
        jitter: Float = 1.4f,
        passes: Int = 2,
    ) {
        val rng = Random(seed)
        val length = hypot(end.x - start.x, end.y - start.y)
        // Aim for ~6 dp segments. We don't have density here, so use an
        // empirical pixel value; callers using bigger components will
        // pass through Modifier.size which already works in pixels here.
        val segmentLen = 14f
        val segments = (length / segmentLen).toInt().coerceAtLeast(4)
        val dx = (end.x - start.x) / segments
        val dy = (end.y - start.y) / segments
        // Perpendicular unit vector for jitter offsets.
        val perpX = -(end.y - start.y) / length
        val perpY = (end.x - start.x) / length

        repeat(passes) { pass ->
            val path = Path()
            var cx = start.x + (rng.nextFloat() - 0.5f) * jitter
            var cy = start.y + (rng.nextFloat() - 0.5f) * jitter
            path.moveTo(cx, cy)
            for (i in 1..segments) {
                val nx = start.x + dx * i
                val ny = start.y + dy * i
                val off = (rng.nextFloat() - 0.5f) * jitter * 2f
                cx = nx + perpX * off
                cy = ny + perpY * off
                path.lineTo(cx, cy)
            }
            // Outer pass slightly thicker and fainter, inner pass crisp.
            val passAlpha = if (pass == 0) 0.55f else 0.85f
            val passWidth = if (pass == 0) strokeWidth * 1.4f else strokeWidth
            drawPath(
                path = path,
                color = color.copy(alpha = color.alpha * passAlpha),
                style = Stroke(
                    width = passWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }

    /**
     * Draw a sketchy rounded-rectangle border, traced as four sides plus
     * arcs. Imperfect corner closures are deliberate — a hand-drawn box
     * almost never closes exactly.
     */
    fun DrawScope.sketchyRoundedRect(
        rect: Rect,
        color: Color,
        strokeWidth: Float,
        cornerRadius: Float,
        seed: Int,
        jitter: Float = 1.6f,
    ) {
        val r = cornerRadius.coerceAtMost(rect.width / 2f).coerceAtMost(rect.height / 2f)
        val rng = Random(seed)

        // Helper: small overshoot so corners don't always meet.
        fun overshoot(): Float = (rng.nextFloat() - 0.5f) * 4f

        // Top edge.
        sketchyLine(
            start = Offset(rect.left + r + overshoot(), rect.top + overshoot() * 0.5f),
            end = Offset(rect.right - r + overshoot(), rect.top + overshoot() * 0.5f),
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 1,
            jitter = jitter,
        )
        // Right edge.
        sketchyLine(
            start = Offset(rect.right + overshoot() * 0.5f, rect.top + r + overshoot()),
            end = Offset(rect.right + overshoot() * 0.5f, rect.bottom - r + overshoot()),
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 2,
            jitter = jitter,
        )
        // Bottom edge.
        sketchyLine(
            start = Offset(rect.right - r + overshoot(), rect.bottom + overshoot() * 0.5f),
            end = Offset(rect.left + r + overshoot(), rect.bottom + overshoot() * 0.5f),
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 3,
            jitter = jitter,
        )
        // Left edge.
        sketchyLine(
            start = Offset(rect.left + overshoot() * 0.5f, rect.bottom - r + overshoot()),
            end = Offset(rect.left + overshoot() * 0.5f, rect.top + r + overshoot()),
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 4,
            jitter = jitter,
        )
        // Corner arcs, drawn as short polylines.
        sketchyArc(
            center = Offset(rect.left + r, rect.top + r),
            radius = r,
            startAngle = 180f,
            sweep = 90f,
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 5,
            jitter = jitter,
        )
        sketchyArc(
            center = Offset(rect.right - r, rect.top + r),
            radius = r,
            startAngle = 270f,
            sweep = 90f,
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 6,
            jitter = jitter,
        )
        sketchyArc(
            center = Offset(rect.right - r, rect.bottom - r),
            radius = r,
            startAngle = 0f,
            sweep = 90f,
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 7,
            jitter = jitter,
        )
        sketchyArc(
            center = Offset(rect.left + r, rect.bottom - r),
            radius = r,
            startAngle = 90f,
            sweep = 90f,
            color = color,
            strokeWidth = strokeWidth,
            seed = seed + 8,
            jitter = jitter,
        )
    }

    /**
     * Hand-drawn arc as a short polyline so it shares the same visual
     * vocabulary as [sketchyLine].
     */
    fun DrawScope.sketchyArc(
        center: Offset,
        radius: Float,
        startAngle: Float,
        sweep: Float,
        color: Color,
        strokeWidth: Float,
        seed: Int,
        jitter: Float = 1.6f,
    ) {
        val rng = Random(seed)
        val steps = (sweep / 8f).toInt().coerceAtLeast(6)
        val path = Path()
        for (i in 0..steps) {
            val angle = Math.toRadians((startAngle + sweep * i / steps).toDouble())
            val rOff = radius + (rng.nextFloat() - 0.5f) * jitter
            val x = center.x + cos(angle).toFloat() * rOff
            val y = center.y + sin(angle).toFloat() * rOff
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.85f),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }

    /**
     * Cross-hatch fill inside [rect]. Two passes of parallel diagonals
     * at +45° and -45°. The [density] controls line spacing in pixels.
     */
    fun DrawScope.crossHatch(
        rect: Rect,
        color: Color,
        density: Float = 6f,
        strokeWidth: Float = 0.8f,
        seed: Int,
        secondPass: Boolean = true,
    ) {
        val rng = Random(seed)
        // First pass: NW-to-SE diagonals.
        var t = -rect.height
        while (t < rect.width) {
            val startX = rect.left + t
            val startY = rect.top
            val endX = startX + rect.height
            val endY = rect.bottom
            // Clip ends to the rectangle.
            val (sx, sy) = clip(startX, startY, endX, endY, rect)
            val (ex, ey) = clip(endX, endY, startX, startY, rect)
            val pressure = 0.55f + rng.nextFloat() * 0.35f
            drawLine(
                color = color.copy(alpha = color.alpha * pressure),
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            t += density
        }
        if (!secondPass) return
        // Second pass: NE-to-SW diagonals, lighter.
        t = -rect.height
        while (t < rect.width) {
            val startX = rect.right - t
            val startY = rect.top
            val endX = startX - rect.height
            val endY = rect.bottom
            val (sx, sy) = clip(startX, startY, endX, endY, rect)
            val (ex, ey) = clip(endX, endY, startX, startY, rect)
            val pressure = 0.35f + rng.nextFloat() * 0.25f
            drawLine(
                color = color.copy(alpha = color.alpha * pressure),
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            t += density * 1.4f
        }
    }

    /**
     * Smudge: a soft elliptical wash rendered as several concentric
     * faint circles. Useful for shaded areas behind important numbers
     * (e.g., a dark halo behind the daily-calorie total).
     */
    fun DrawScope.smudge(
        center: Offset,
        radius: Float,
        color: Color,
        seed: Int,
    ) {
        val rng = Random(seed)
        val rings = 12
        for (i in 0 until rings) {
            val r = radius * (1f - i / rings.toFloat()) + rng.nextFloat() * 2f
            val alpha = (color.alpha * (0.04f + 0.025f * i)).coerceAtMost(0.5f)
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = r,
                center = center.copy(
                    x = center.x + (rng.nextFloat() - 0.5f) * 1.5f,
                    y = center.y + (rng.nextFloat() - 0.5f) * 1.5f,
                ),
            )
        }
    }

    /**
     * Path effect that approximates a graphite-on-paper "skip": short
     * dashes with random gaps. Used for emphasis underlines.
     */
    fun graphiteDash(seed: Int): PathEffect {
        // Vary phase by seed so callers don't all align.
        return PathEffect.dashPathEffect(floatArrayOf(8f, 4f, 2f, 6f), phase = (seed % 9).toFloat())
    }

    private fun clip(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        rect: Rect,
    ): Pair<Float, Float> {
        // Cohen–Sutherland-ish clamp: assumes line is a 45° diagonal so
        // we just clamp x and y independently to the rect bounds along
        // the parametric line. This is good enough for hatching where
        // visual exactness is unimportant.
        val cx = x1.coerceIn(rect.left, rect.right)
        val cy = y1.coerceIn(rect.top, rect.bottom)
        return cx to cy
    }
}

/** Convenience extension for a sketchy underline with the dash effect. */
fun DrawScope.sketchUnderline(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float,
    seed: Int,
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
        pathEffect = SketchStroke.graphiteDash(seed),
    )
}

/** Tiny helper for components that need to expose a deterministic seed
 *  derived from a stable key (e.g., a card title). Hashing keeps look
 *  consistent for the same content. */
fun keySeed(key: Any?): Int = key?.hashCode()?.let { it and 0x7FFFFFFF } ?: 0

/** Useful for components that want a per-canvas seed independent of size. */
fun DrawScope.canvasSeed(extra: Int = 0): Int =
    (size.width.toInt() * 31 + size.height.toInt() + extra) and 0x7FFFFFFF

/** Convert a size to a centered rect with [inset] padding. */
fun Size.toInsetRect(inset: Float): Rect = Rect(
    left = inset,
    top = inset,
    right = width - inset,
    bottom = height - inset,
)
