package com.graphiteplate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphiteplate.data.prefs.MacroSplit
import com.graphiteplate.ui.sketch.SketchButton
import com.graphiteplate.ui.sketch.SketchButtonStyle
import com.graphiteplate.ui.sketch.SketchCard
import com.graphiteplate.ui.sketch.SketchProgressBar
import com.graphiteplate.ui.sketch.SketchSectionHeading
import com.graphiteplate.ui.sketch.SketchTextField
import com.graphiteplate.ui.theme.SketchTheme
import com.graphiteplate.ui.vm.SettingsViewModel
import com.graphiteplate.ui.vm.graphiteViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = graphiteViewModel { repo -> SettingsViewModel(repo) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = SketchTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.graphite8B,
                )
                Text(
                    text = "Daily goal and macro split",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.graphiteHB,
                )
            }
            SketchButton(onClick = onBack, text = "BACK", style = SketchButtonStyle.Ghost)
        }

        SketchSectionHeading(text = "Calorie goal")
        SketchCard(modifier = Modifier.fillMaxWidth(), key = "goal-card") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SketchTextField(
                    value = state.draftGoal,
                    onValueChange = viewModel::setDraftGoal,
                    label = "DAILY CALORIE GOAL",
                    placeholder = "2000",
                    keyboardType = KeyboardType.Number,
                    suffix = "kcal/day",
                )
                SketchButton(
                    onClick = viewModel::saveGoal,
                    text = "SAVE GOAL",
                    style = SketchButtonStyle.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        SketchSectionHeading(text = "Macro split")
        MacroSplitEditor(
            split = state.macroSplit,
            onChange = viewModel::setMacroSplit,
        )
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun MacroSplitEditor(
    split: MacroSplit,
    onChange: (MacroSplit) -> Unit,
) {
    val colors = SketchTheme.colors
    // Local sliders driven by the persisted split, but the user can drag them
    // freely; on commit we re-normalize so the three values still sum to 1.
    var protein by remember(split.proteinPct) { mutableFloatStateOf(split.proteinPct.toFloat()) }
    var carbs by remember(split.carbsPct) { mutableFloatStateOf(split.carbsPct.toFloat()) }
    var fat by remember(split.fatPct) { mutableFloatStateOf(split.fatPct.toFloat()) }

    fun commit() {
        val sum = (protein + carbs + fat).coerceAtLeast(0.001f)
        onChange(
            MacroSplit(
                proteinPct = (protein / sum).toDouble(),
                carbsPct = (carbs / sum).toDouble(),
                fatPct = (fat / sum).toDouble(),
            )
        )
    }

    SketchCard(modifier = Modifier.fillMaxWidth(), key = "macro-card") {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            MacroSliderRow(
                label = "Protein",
                value = protein,
                onValueChange = { protein = it; commit() },
                valueLabel = "${(protein * 100).roundToInt()}%",
            )
            MacroSliderRow(
                label = "Carbs",
                value = carbs,
                onValueChange = { carbs = it; commit() },
                valueLabel = "${(carbs * 100).roundToInt()}%",
            )
            MacroSliderRow(
                label = "Fat",
                value = fat,
                onValueChange = { fat = it; commit() },
                valueLabel = "${(fat * 100).roundToInt()}%",
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Values are normalized to 100% on save.",
                style = MaterialTheme.typography.labelMedium,
                color = colors.graphite2H,
            )
        }
    }
}

@Composable
private fun MacroSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueLabel: String,
) {
    val colors = SketchTheme.colors
    Column {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = colors.graphite8B,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.titleMedium,
                color = colors.graphite8B,
            )
        }
        Spacer(Modifier.height(4.dp))
        // We display the current proportion via the sketchy bar and use a
        // standard Slider beneath it — the slider needs no Material visual
        // since the bar carries the full meaning.
        SketchProgressBar(progress = value, seedKey = label)
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = colors.graphite8B,
                activeTrackColor = colors.graphite6B,
                inactiveTrackColor = colors.graphite2H,
            ),
        )
    }
}
