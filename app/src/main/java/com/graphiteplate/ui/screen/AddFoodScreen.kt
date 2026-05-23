package com.graphiteplate.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.graphiteplate.domain.model.MealType
import com.graphiteplate.ui.sketch.SketchButton
import com.graphiteplate.ui.sketch.SketchButtonStyle
import com.graphiteplate.ui.sketch.SketchCard
import com.graphiteplate.ui.sketch.SketchSectionHeading
import com.graphiteplate.ui.sketch.SketchStroke.crossHatch
import com.graphiteplate.ui.sketch.SketchStroke.sketchyRoundedRect
import com.graphiteplate.ui.sketch.SketchTextField
import com.graphiteplate.ui.sketch.keySeed
import com.graphiteplate.ui.theme.SketchTheme
import com.graphiteplate.ui.vm.AddFoodViewModel
import com.graphiteplate.ui.vm.graphiteViewModel
import java.util.Locale

@Composable
fun AddFoodScreen(
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel: AddFoodViewModel = graphiteViewModel { repo -> AddFoodViewModel(repo) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        SketchSectionHeading(text = "Log a food")

        SketchCard(modifier = Modifier.fillMaxWidth(), key = "form") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SketchTextField(
                    value = state.name,
                    onValueChange = viewModel::setName,
                    label = "NAME",
                    placeholder = "Oatmeal with banana",
                )
                SketchTextField(
                    value = state.calories,
                    onValueChange = viewModel::setCalories,
                    label = "CALORIES",
                    placeholder = "350",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "kcal",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SketchTextField(
                        value = state.proteinG,
                        onValueChange = viewModel::setProtein,
                        label = "PROTEIN",
                        placeholder = "0",
                        keyboardType = KeyboardType.Decimal,
                        suffix = "g",
                        modifier = Modifier.weight(1f),
                    )
                    SketchTextField(
                        value = state.carbsG,
                        onValueChange = viewModel::setCarbs,
                        label = "CARBS",
                        placeholder = "0",
                        keyboardType = KeyboardType.Decimal,
                        suffix = "g",
                        modifier = Modifier.weight(1f),
                    )
                    SketchTextField(
                        value = state.fatG,
                        onValueChange = viewModel::setFat,
                        label = "FAT",
                        placeholder = "0",
                        keyboardType = KeyboardType.Decimal,
                        suffix = "g",
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "MEAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = SketchTheme.colors.graphiteHB,
                )
                MealTypeRow(
                    selected = state.mealType,
                    onSelect = viewModel::setMealType,
                )
            }
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = SketchTheme.colors.sanguine,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SketchButton(
                onClick = onCancel,
                text = "CANCEL",
                style = SketchButtonStyle.Secondary,
                modifier = Modifier.weight(1f),
            )
            SketchButton(
                onClick = viewModel::save,
                text = if (state.isSaving) "SAVING…" else "SAVE",
                style = SketchButtonStyle.Primary,
                enabled = state.canSave && !state.isSaving,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MealTypeRow(
    selected: MealType,
    onSelect: (MealType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MealType.entries.forEach { meal ->
            MealChip(
                meal = meal,
                isSelected = meal == selected,
                onClick = { onSelect(meal) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MealChip(
    meal: MealType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SketchTheme.colors
    Box(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick)
            .drawBehind {
                val pad = 3f
                val rect = Rect(pad, pad, size.width - pad, size.height - pad)
                if (isSelected) {
                    crossHatch(
                        rect = rect,
                        color = colors.graphite6B.copy(alpha = 0.45f),
                        density = 5f,
                        strokeWidth = 1.0f,
                        seed = keySeed(meal.name) + 31,
                    )
                }
                sketchyRoundedRect(
                    rect = rect,
                    color = if (isSelected) colors.graphite8B else colors.graphite2B,
                    strokeWidth = if (isSelected) 2.0f else 1.4f,
                    cornerRadius = rect.height / 2f,
                    seed = keySeed(meal.name) + 7,
                    jitter = 1.1f,
                )
            }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = meal.display.uppercase(Locale.ENGLISH),
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) colors.graphite8B else colors.graphite2B,
        )
    }
}
