package com.graphiteplate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphiteplate.domain.model.WeightEntry
import com.graphiteplate.ui.sketch.SketchButton
import com.graphiteplate.ui.sketch.SketchButtonStyle
import com.graphiteplate.ui.sketch.SketchCard
import com.graphiteplate.ui.sketch.SketchSectionHeading
import com.graphiteplate.ui.sketch.SketchTextField
import com.graphiteplate.ui.theme.SketchTheme
import com.graphiteplate.ui.vm.WeightViewModel
import com.graphiteplate.ui.vm.graphiteViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val WEIGHT_DAY = DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH)

@Composable
fun WeightScreen(onBack: () -> Unit) {
    val viewModel: WeightViewModel = graphiteViewModel { repo -> WeightViewModel(repo) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = SketchTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weight",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.graphite8B,
                    )
                    val latestText = state.latest?.let { "Latest: ${"%.1f".format(it.weightKg)} kg" } ?: "No entries yet"
                    Text(
                        text = latestText,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.graphiteHB,
                    )
                }
                SketchButton(onClick = onBack, text = "BACK", style = SketchButtonStyle.Ghost)
            }
        }
        item {
            SketchCard(modifier = Modifier.fillMaxWidth(), key = "weight-form") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SketchTextField(
                        value = state.draftWeight,
                        onValueChange = viewModel::setDraft,
                        label = "TODAY'S WEIGHT",
                        placeholder = "70.5",
                        keyboardType = KeyboardType.Decimal,
                        suffix = "kg",
                    )
                    SketchButton(
                        onClick = viewModel::save,
                        text = "SAVE WEIGHT",
                        style = SketchButtonStyle.Primary,
                        enabled = state.canSave,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        item { SketchSectionHeading(text = "Recent readings") }
        if (state.entries.isEmpty()) {
            item {
                SketchCard(modifier = Modifier.fillMaxWidth(), key = "weights-empty") {
                    Text(
                        text = "No readings yet — once you log a few, you'll see the trend here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.graphite2B,
                    )
                }
            }
        }
        items(state.entries, key = { it.id }) { entry ->
            WeightRow(entry = entry)
        }
    }
}

@Composable
private fun WeightRow(entry: WeightEntry) {
    val colors = SketchTheme.colors
    SketchCard(
        modifier = Modifier.fillMaxWidth(),
        key = "weight-${entry.id}",
        contentPadding = 14.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entry.date.format(WEIGHT_DAY),
                style = MaterialTheme.typography.titleMedium,
                color = colors.graphite8B,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "%.1f kg".format(entry.weightKg),
                style = MaterialTheme.typography.titleMedium,
                color = colors.graphite8B,
            )
        }
    }
}
