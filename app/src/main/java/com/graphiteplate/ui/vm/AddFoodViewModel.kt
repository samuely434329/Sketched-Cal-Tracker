package com.graphiteplate.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphiteplate.data.CalorieRepository
import com.graphiteplate.domain.model.MealType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddFoodUiState(
    val name: String = "",
    val calories: String = "",
    val proteinG: String = "",
    val carbsG: String = "",
    val fatG: String = "",
    val mealType: MealType = inferDefaultMealType(),
    val date: LocalDate = LocalDate.now(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,
) {
    /** Save is allowed once the user has entered a name and a calorie value
     *  that parses to a non-negative number. Macros are optional. */
    val canSave: Boolean
        get() = name.isNotBlank() &&
            calories.toDoubleOrNull()?.let { it >= 0.0 } == true
}

class AddFoodViewModel(
    private val repository: CalorieRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodUiState())
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()

    fun setName(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun setCalories(value: String) = _uiState.update { it.copy(calories = sanitize(value)) }
    fun setProtein(value: String) = _uiState.update { it.copy(proteinG = sanitize(value)) }
    fun setCarbs(value: String) = _uiState.update { it.copy(carbsG = sanitize(value)) }
    fun setFat(value: String) = _uiState.update { it.copy(fatG = sanitize(value)) }
    fun setMealType(meal: MealType) = _uiState.update { it.copy(mealType = meal) }

    fun save() {
        val current = _uiState.value
        if (!current.canSave) {
            _uiState.update { it.copy(errorMessage = "Enter at least a name and calories.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.addFood(
                name = current.name.trim(),
                calories = current.calories.toDoubleOrNull() ?: 0.0,
                proteinG = current.proteinG.toDoubleOrNull() ?: 0.0,
                carbsG = current.carbsG.toDoubleOrNull() ?: 0.0,
                fatG = current.fatG.toDoubleOrNull() ?: 0.0,
                mealType = current.mealType,
                date = current.date,
            )
            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }

    /** Strip anything that isn't part of a positive decimal number. */
    private fun sanitize(value: String): String =
        value.filter { it.isDigit() || it == '.' }
}

/** Heuristic: choose breakfast/lunch/dinner/snack based on local time. */
private fun inferDefaultMealType(): MealType {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..10 -> MealType.Breakfast
        in 11..14 -> MealType.Lunch
        in 17..21 -> MealType.Dinner
        else -> MealType.Snack
    }
}
