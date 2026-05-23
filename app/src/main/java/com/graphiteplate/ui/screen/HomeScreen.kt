package com.graphiteplate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphiteplate.domain.model.FoodEntry
import com.graphiteplate.domain.model.MealType
import com.graphiteplate.ui.sketch.SketchButton
import com.graphiteplate.ui.sketch.SketchButtonStyle
import com.graphiteplate.ui.sketch.SketchCard
import com.graphiteplate.ui.sketch.SketchDivider
import com.graphiteplate.ui.sketch.SketchProgressBar
import com.graphiteplate.ui.sketch.SketchProgressRing
import com.graphiteplate.ui.sketch.SketchSectionHeading
import com.graphiteplate.ui.sketch.SketchTag
import com.graphiteplate.ui.theme.SketchTheme
import com.graphiteplate.ui.vm.HomeUiState
import com.graphiteplate.ui.vm.HomeViewModel
import com.graphiteplate.ui.vm.graphiteViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val DATE_HEADER = DateTimeFormatter.ofPattern("EEEE  ·  d MMM yyyy", Locale.ENGLISH)

@Composable
fun HomeScreen(
    onAddFoodClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onWeightClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val viewModel: HomeViewModel = graphiteViewModel { repo -> HomeViewModel(repo) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onAddFoodClick = onAddFoodClick,
        onHistoryClick = onHistoryClick,
        onWeightClick = onWeightClick,
        onSettingsClick = onSettingsClick,
        onDelete = viewModel::deleteEntry,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onAddFoodClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onWeightClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDelete: (FoodEntry) -> Unit,
) {
    val colors = SketchTheme.colors
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Graphite Plate",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.graphite8B,
                    )
                    Text(
                        text = state.date.format(DATE_HEADER).uppercase(Locale.ENGLISH),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.graphiteHB,
                    )
                }
                SketchButton(
                    onClick = onSettingsClick,
                    text = "GOAL",
                    style = SketchButtonStyle.Ghost,
                )
            }
        }

        item { CalorieRingCard(state = state) }

        item { MacroBreakdownCard(state = state) }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SketchButton(
                    onClick = onAddFoodClick,
                    text = "+  LOG FOOD",
                    style = SketchButtonStyle.Primary,
                    modifier = Modifier.weight(1f),
                )
                SketchButton(
                    onClick = onWeightClick,
                    text = "WEIGHT",
                    style = SketchButtonStyle.Secondary,
                )
                SketchButton(
                    onClick = onHistoryClick,
                    text = "HISTORY",
                    style = SketchButtonStyle.Secondary,
                )
            }
        }

        item {
            SketchSectionHeading(text = "Today's plate")
        }

        MealType.entries.forEach { meal ->
            val entries = state.byMeal[meal].orEmpty()
            item(key = "section-$meal") {
                MealSection(
                    meal = meal,
                    entries = entries,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
private fun CalorieRingCard(state: HomeUiState) {
    val colors = SketchTheme.colors
    SketchCard(
        modifier = Modifier.fillMaxWidth(),
        key = "ring-${state.date}",
        emphasized = true,
        contentPadding = 20.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SketchProgressRing(
                progress = state.progress,
                seedKey = "ring-${state.date}",
                size = 168.dp,
                strokeWidth = 8.dp,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.consumedCalories.roundToInt().toString(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = colors.graphite8B,
                    )
                    Text(
                        text = "of ${state.goalCalories} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.graphiteHB,
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                StatLine(
                    label = "Remaining",
                    value = state.remaining.coerceAtLeast(0).toString() + " kcal",
                )
                Spacer(Modifier.height(8.dp))
                StatLine(
                    label = "Logged",
                    value = state.consumedCalories.roundToInt().toString() + " kcal",
                )
                Spacer(Modifier.height(8.dp))
                StatLine(
                    label = "Goal",
                    value = state.goalCalories.toString() + " kcal",
                )
                Spacer(Modifier.height(12.dp))
                if (state.progress > 1f) {
                    Text(
                        text = "Over goal by ${(state.consumedCalories - state.goalCalories).roundToInt()} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.sanguine,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    val colors = SketchTheme.colors
    Column {
        Text(
            text = label.uppercase(Locale.ENGLISH),
            style = MaterialTheme.typography.labelMedium,
            color = colors.graphite2H,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = colors.graphite8B,
        )
    }
}

@Composable
private fun MacroBreakdownCard(state: HomeUiState) {
    val colors = SketchTheme.colors
    val total = (state.proteinG + state.carbsG + state.fatG).coerceAtLeast(0.001)
    val pPct = state.proteinG / total
    val cPct = state.carbsG / total
    val fPct = state.fatG / total

    SketchCard(
        modifier = Modifier.fillMaxWidth(),
        key = "macros-${state.date}",
        shaded = false,
    ) {
        Column {
            Text(
                text = "MACROS",
                style = MaterialTheme.typography.labelMedium,
                color = colors.graphite2H,
            )
            Spacer(Modifier.height(10.dp))
            MacroRow("Protein", state.proteinG, pPct.toFloat(), seed = "p-${state.date}")
            Spacer(Modifier.height(10.dp))
            MacroRow("Carbs", state.carbsG, cPct.toFloat(), seed = "c-${state.date}")
            Spacer(Modifier.height(10.dp))
            MacroRow("Fat", state.fatG, fPct.toFloat(), seed = "f-${state.date}")
        }
    }
}

@Composable
private fun MacroRow(label: String, grams: Double, pct: Float, seed: String) {
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
                text = "${grams.roundToInt()} g",
                style = MaterialTheme.typography.titleMedium,
                color = colors.graphite8B,
            )
        }
        Spacer(Modifier.height(4.dp))
        SketchProgressBar(progress = pct, seedKey = seed)
    }
}

@Composable
private fun MealSection(
    meal: MealType,
    entries: List<FoodEntry>,
    onDelete: (FoodEntry) -> Unit,
) {
    val colors = SketchTheme.colors
    SketchCard(
        modifier = Modifier.fillMaxWidth(),
        key = "meal-${meal.name}",
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SketchTag(text = meal.display.uppercase(Locale.ENGLISH))
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${entries.sumOf { it.calories }.roundToInt()} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.graphite8B,
                )
            }
            if (entries.isEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Nothing logged yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.graphite2H,
                )
            } else {
                Spacer(Modifier.height(10.dp))
                entries.forEachIndexed { index, entry ->
                    if (index > 0) {
                        SketchDivider(seedKey = "div-${entry.id}")
                    }
                    FoodEntryRow(entry = entry, onDelete = onDelete)
                }
            }
        }
    }
}

@Composable
private fun FoodEntryRow(entry: FoodEntry, onDelete: (FoodEntry) -> Unit) {
    val colors = SketchTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleMedium,
                color = colors.graphite8B,
            )
            val macros = listOfNotNull(
                if (entry.proteinG > 0) "P ${entry.proteinG.roundToInt()}" else null,
                if (entry.carbsG > 0) "C ${entry.carbsG.roundToInt()}" else null,
                if (entry.fatG > 0) "F ${entry.fatG.roundToInt()}" else null,
            ).joinToString("  ·  ")
            if (macros.isNotEmpty()) {
                Text(
                    text = macros,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.graphite2H,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "${entry.calories.roundToInt()} kcal",
            style = MaterialTheme.typography.titleMedium,
            color = colors.graphite8B,
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.padding(start = 4.dp)) {
            SketchButton(
                onClick = { onDelete(entry) },
                text = "✕",
                style = SketchButtonStyle.Ghost,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 10.dp, vertical = 4.dp,
                ),
            )
        }
    }
}
