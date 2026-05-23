package com.graphiteplate.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The complete graphite-on-paper palette used by the sketch theme.
 *
 * Values are derived from a real 2B-pencil-on-cream-paper reference:
 *  - Paper tones are warm off-whites with subtle cream tint.
 *  - Graphite tones range from a faint 6H trace to a deep 8B core.
 *  - A single muted accent (sanguine red) is reserved for highlights
 *    such as the daily-goal progress overshoot, kept restrained so the
 *    overall feel stays monochromatic.
 */
@Immutable
data class SketchColors(
    val paper: Color,
    val paperDark: Color,
    val paperShadow: Color,
    val graphite6H: Color,
    val graphite2H: Color,
    val graphiteHB: Color,
    val graphite2B: Color,
    val graphite6B: Color,
    val graphite8B: Color,
    val sanguine: Color,
    val highlight: Color,
)

val LightSketchColors = SketchColors(
    paper = Color(0xFFF4EFE6),
    paperDark = Color(0xFFEDE5D6),
    paperShadow = Color(0xFFD9CFBC),
    graphite6H = Color(0xFFB8B0A4),
    graphite2H = Color(0xFF8A8278),
    graphiteHB = Color(0xFF5C564F),
    graphite2B = Color(0xFF3A3631),
    graphite6B = Color(0xFF2A2724),
    graphite8B = Color(0xFF14120F),
    sanguine = Color(0xFF7A2E1F),
    highlight = Color(0xFFFFF7E2),
)

val LocalSketchColors = compositionLocalOf { LightSketchColors }
