package com.graphiteplate.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graphiteplate.data.CalorieRepository
import com.graphiteplate.data.prefs.MacroSplit
import com.graphiteplate.domain.model.DailyTotal
import com.graphiteplate.domain.model.WeightEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val totals: List<DailyTotal> = emptyList(),
    val goal: Int = 2000,
    val isLoading: Boolean = true,
)

class HistoryViewModel(
    private val repository: CalorieRepository,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.observeDailyTotals(),
        repository.calorieGoal,
    ) { totals, goal ->
        HistoryUiState(totals = totals, goal = goal, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState(),
    )
}

data class WeightUiState(
    val entries: List<WeightEntry> = emptyList(),
    val latest: WeightEntry? = null,
    val draftWeight: String = "",
    val isLoading: Boolean = true,
) {
    val canSave: Boolean
        get() = draftWeight.toDoubleOrNull()?.let { it > 0.0 } == true
}

class WeightViewModel(
    private val repository: CalorieRepository,
) : ViewModel() {

    private val draft = MutableStateFlow("")

    val uiState: StateFlow<WeightUiState> = combine(
        repository.observeRecentWeights(),
        repository.observeLatestWeight(),
        draft,
    ) { entries, latest, draftValue ->
        WeightUiState(
            entries = entries,
            latest = latest,
            draftWeight = draftValue,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WeightUiState(),
    )

    fun setDraft(value: String) {
        draft.value = value.filter { it.isDigit() || it == '.' }
    }

    fun save() {
        val v = draft.value.toDoubleOrNull() ?: return
        viewModelScope.launch {
            repository.addWeight(v)
            draft.value = ""
        }
    }
}

data class SettingsUiState(
    val goal: Int = 2000,
    val macroSplit: MacroSplit = MacroSplit(0.30, 0.40, 0.30),
    val draftGoal: String = "2000",
)

class SettingsViewModel(
    private val repository: CalorieRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.calorieGoal, repository.macroSplit) { goal, split ->
                goal to split
            }.collect { (goal, split) ->
                _ui.update { current ->
                    current.copy(
                        goal = goal,
                        macroSplit = split,
                        // Only overwrite the draft if the user hasn't been editing.
                        draftGoal = if (current.draftGoal == current.goal.toString()) {
                            goal.toString()
                        } else current.draftGoal,
                    )
                }
            }
        }
    }

    fun setDraftGoal(value: String) {
        _ui.update { it.copy(draftGoal = value.filter { ch -> ch.isDigit() }) }
    }

    fun saveGoal() {
        val v = _ui.value.draftGoal.toIntOrNull() ?: return
        viewModelScope.launch { repository.setCalorieGoal(v) }
    }

    fun setMacroSplit(split: MacroSplit) {
        viewModelScope.launch { repository.setMacroSplit(split) }
    }
}
