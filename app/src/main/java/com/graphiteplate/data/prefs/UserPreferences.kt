package com.graphiteplate.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * Persists the small set of user preferences this app needs:
 *   - Daily calorie goal (default 2000 kcal)
 *   - Macro split percentages (protein/carbs/fat) for the goal-bar
 *     visualization. Default 30/40/30 — a balanced split that's easy to
 *     adjust later in Settings.
 */
class UserPreferences(private val context: Context) {

    private object Keys {
        val CALORIE_GOAL = intPreferencesKey("calorie_goal")
        val PROTEIN_PCT = doublePreferencesKey("protein_pct")
        val CARBS_PCT = doublePreferencesKey("carbs_pct")
        val FAT_PCT = doublePreferencesKey("fat_pct")
    }

    val calorieGoal: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.CALORIE_GOAL] ?: 2000
    }

    val macroSplit: Flow<MacroSplit> = context.dataStore.data.map { prefs ->
        MacroSplit(
            proteinPct = prefs[Keys.PROTEIN_PCT] ?: 0.30,
            carbsPct = prefs[Keys.CARBS_PCT] ?: 0.40,
            fatPct = prefs[Keys.FAT_PCT] ?: 0.30,
        )
    }

    suspend fun setCalorieGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CALORIE_GOAL] = goal.coerceAtLeast(0)
        }
    }

    suspend fun setMacroSplit(split: MacroSplit) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PROTEIN_PCT] = split.proteinPct
            prefs[Keys.CARBS_PCT] = split.carbsPct
            prefs[Keys.FAT_PCT] = split.fatPct
        }
    }
}

/**
 * Macro split as fractions of total calories. Validated by clients —
 * the store does not enforce sum=1.0 because users may want to tweak
 * one slider before another and we don't want to flicker.
 */
data class MacroSplit(
    val proteinPct: Double,
    val carbsPct: Double,
    val fatPct: Double,
)
