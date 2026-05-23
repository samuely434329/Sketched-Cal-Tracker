package com.graphiteplate.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphiteplate.data.CalorieRepository
import com.graphiteplate.domain.model.FoodEntry
import com.graphiteplate.domain.model.MealType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * State for the Home screen. The ring around the calorie total reflects
 * [progress] (consumed/goal); macros are summed up per gram regardless
 * of whether the user supplied them, so missing macros simply read as
 * zero rather than skewing the totals.
 */
data class HomeUiState(
    val date: LocalDate = LocalDate.now(),
    val goalCalories: Int = 2000,
    val consumedCalories: Double = 0.0,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
    val byMeal: Map<MealType, List<FoodEntry>> = emptyMap(),
    val isLoading: Boolean = true,
) {
    val remaining: Int get() = (goalCalories - consumedCalories).toInt()
    val progress: Float
        get() = if (goalCalories > 0) (consumedCalories / goalCalories).toFloat() else 0f
}

class HomeViewModel(
    private val repository: CalorieRepository,
) : ViewModel() {

    private val today = LocalDate.now()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeFoodForDay(today),
        repository.calorieGoal,
    ) { entries, goal ->
        val byMeal = entries.groupBy { it.mealType }
        HomeUiState(
            date = today,
            goalCalories = goal,
            consumedCalories = entries.sumOf { it.calories },
            proteinG = entries.sumOf { it.proteinG },
            carbsG = entries.sumOf { it.carbsG },
            fatG = entries.sumOf { it.fatG },
            byMeal = byMeal,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch { repository.deleteFood(entry) }
    }
}
