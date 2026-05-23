package com.graphiteplate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Top-level theme entry point. Provides:
 *  - A custom [SketchColors] palette via [LocalSketchColors].
 *  - A Material 3 [lightColorScheme] mapped onto graphite-on-paper tones,
 *    so any default Material component (e.g. [Snackbar], [DatePicker])
 *    inherits the correct surface colors.
 *  - The [SketchTypography] scale.
 *
 * Dark theme is intentionally omitted: a "pencil sketch on dark paper"
 * effect requires a very different visual language (chalk on slate)
 * and was out of scope for the initial design.
 */
@Composable
fun SketchTheme(
    content: @Composable () -> Unit,
) {
    val sketchColors = LightSketchColors
    val materialColors = lightColorScheme(
        primary = sketchColors.graphite6B,
        onPrimary = sketchColors.paper,
        primaryContainer = sketchColors.paperDark,
        onPrimaryContainer = sketchColors.graphite8B,
        secondary = sketchColors.sanguine,
        onSecondary = sketchColors.paper,
        background = sketchColors.paper,
        onBackground = sketchColors.graphite8B,
        surface = sketchColors.paper,
        onSurface = sketchColors.graphite8B,
        surfaceVariant = sketchColors.paperDark,
        onSurfaceVariant = sketchColors.graphite2B,
        outline = sketchColors.graphite2H,
        error = sketchColors.sanguine,
        onError = sketchColors.paper,
    )

    CompositionLocalProvider(LocalSketchColors provides sketchColors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = SketchTypography,
            content = content,
        )
    }
}

/** Convenience accessor for screen code: `SketchTheme.colors.graphiteHB`. */
object SketchTheme {
    val colors: SketchColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSketchColors.current
}
