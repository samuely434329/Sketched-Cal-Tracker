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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphiteplate.domain.model.DailyTotal
import com.graphiteplate.ui.sketch.SketchButton
import com.graphiteplate.ui.sketch.SketchButtonStyle
import com.graphiteplate.ui.sketch.SketchCard
import com.graphiteplate.ui.sketch.SketchProgressBar
import com.graphiteplate.ui.sketch.SketchSectionHeading
import com.graphiteplate.ui.theme.SketchTheme
import com.graphiteplate.ui.vm.HistoryViewModel
import com.graphiteplate.ui.vm.graphiteViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val DAY_FORMAT = DateTimeFormatter.ofPattern("EEEE  ·  d MMM", Locale.ENGLISH)

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val viewModel: HistoryViewModel = graphiteViewModel { repo -> HistoryViewModel(repo) }
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
                        text = "History",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.graphite8B,
                    )
                    Text(
                        text = "Last ${state.totals.size} days",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.graphiteHB,
                    )
                }
                SketchButton(
                    onClick = onBack,
                    text = "BACK",
                    style = SketchButtonStyle.Ghost,
                )
            }
        }
        item { SketchSectionHeading(text = "Calorie totals") }
        if (state.totals.isEmpty() && !state.isLoading) {
            item {
                SketchCard(modifier = Modifier.fillMaxWidth(), key = "empty") {
                    Text(
                        text = "Nothing logged yet. Once you start logging, days will collect here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.graphite2B,
                    )
                }
            }
        }
        items(state.totals, key = { it.date.toString() }) { total ->
            DailyRow(total = total, goal = state.goal)
        }
    }
}

@Composable
private fun DailyRow(total: DailyTotal, goal: Int) {
    val colors = SketchTheme.colors
    val progress = if (goal > 0) (total.calories / goal).toFloat() else 0f
    SketchCard(
        modifier = Modifier.fillMaxWidth(),
        key = "day-${total.date}",
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = total.date.format(DAY_FORMAT),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.graphite8B,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${total.calories.roundToInt()} / $goal kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.graphite8B,
                )
            }
            Spacer(Modifier.height(8.dp))
            SketchProgressBar(progress = progress, seedKey = "history-${total.date}")
            Spacer(Modifier.height(8.dp))
            Text(
                text = "P ${total.proteinG.roundToInt()} g  ·  C ${total.carbsG.roundToInt()} g  ·  F ${total.fatG.roundToInt()} g",
                style = MaterialTheme.typography.labelMedium,
                color = colors.graphite2H,
            )
        }
    }
}
