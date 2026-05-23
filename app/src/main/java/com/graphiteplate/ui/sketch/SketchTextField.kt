package com.graphiteplate.ui.sketch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.graphiteplate.ui.theme.SketchTheme

/**
 * Single-line text field with a sketchy underline. Label sits above the
 * input in a smaller serif so the form reads like a notebook field.
 */
@Composable
fun SketchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    suffix: String? = null,
) {
    val colors = SketchTheme.colors
    val seed = remember(label) { keySeed(label) + 53 }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.graphiteHB,
        )
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(colors.graphite8B),
            textStyle = LocalTextStyle.current.merge(
                MaterialTheme.typography.bodyLarge.copy(color = colors.graphite8B),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .drawBehind {
                    sketchUnderline(
                        start = Offset(0f, size.height + 6f),
                        end = Offset(size.width, size.height + 6f),
                        color = colors.graphite6B,
                        strokeWidth = 1.6f,
                        seed = seed,
                    )
                    // Reinforcing pass for ink-on-paper density.
                    sketchUnderline(
                        start = Offset(2f, size.height + 8f),
                        end = Offset(size.width - 4f, size.height + 8f),
                        color = colors.graphite2B.copy(alpha = 0.6f),
                        strokeWidth = 1.0f,
                        seed = seed + 1,
                    )
                },
            decorationBox = { inner ->
                androidx.compose.foundation.layout.Box {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.graphite2H,
                        )
                    }
                    inner()
                }
            },
        )
        if (suffix != null) {
            Text(
                text = suffix,
                style = MaterialTheme.typography.labelMedium,
                color = colors.graphite2H,
            )
        }
    }
}
