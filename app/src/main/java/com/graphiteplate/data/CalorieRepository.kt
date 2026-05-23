package com.graphiteplate.data

import com.graphiteplate.data.local.AppDatabase
import com.graphiteplate.data.local.toDomain
import com.graphiteplate.data.local.toEntity
import com.graphiteplate.data.prefs.MacroSplit
import com.graphiteplate.data.prefs.UserPreferences
import com.graphiteplate.domain.model.DailyTotal
import com.graphiteplate.domain.model.FoodEntry
import com.graphiteplate.domain.model.MealType
import com.graphiteplate.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Single entry-point used by all ViewModels. Keeping a thin pass-through
 * repository (rather than UseCases per operation) avoids ceremony at
 * this scale; if the app grows we can split it.
 */
class CalorieRepository(
    private val db: AppDatabase,
    private val prefs: UserPreferences,
) {

    fun observeFoodForDay(date: LocalDate): Flow<List<FoodEntry>> =
        db.foodEntryDao()
            .observeByDay(date.toString())
            .map { rows -> rows.map { it.toDomain() } }

    fun observeDailyTotals(): Flow<List<DailyTotal>> =
        db.foodEntryDao()
            .observeDailyTotals()
            .map { rows -> rows.map { it.toDomain() } }

    suspend fun addFood(
        name: String,
        calories: Double,
        proteinG: Double,
        carbsG: Double,
        fatG: Double,
        mealType: MealType,
        date: LocalDate = LocalDate.now(),
    ): Long {
        val entry = FoodEntry(
            id = 0,
            name = name,
            calories = calories,
            proteinG = proteinG,
            carbsG = carbsG,
            fatG = fatG,
            mealType = mealType,
            date = date,
            createdAt = System.currentTimeMillis(),
        )
        return db.foodEntryDao().insert(entry.toEntity())
    }

    suspend fun deleteFood(entry: FoodEntry) {
        db.foodEntryDao().delete(entry.toEntity())
    }

    fun observeRecentWeights(): Flow<List<WeightEntry>> =
        db.weightEntryDao()
            .observeRecent()
            .map { rows -> rows.map { it.toDomain() } }

    fun observeLatestWeight(): Flow<WeightEntry?> =
        db.weightEntryDao().observeLatest().map { it?.toDomain() }

    suspend fun addWeight(weightKg: Double, date: LocalDate = LocalDate.now()): Long {
        val entry = WeightEntry(
            id = 0,
            weightKg = weightKg,
            date = date,
            createdAt = System.currentTimeMillis(),
        )
        return db.weightEntryDao().insert(entry.toEntity())
    }

    val calorieGoal: Flow<Int> = prefs.calorieGoal
    val macroSplit: Flow<MacroSplit> = prefs.macroSplit

    suspend fun setCalorieGoal(goal: Int) = prefs.setCalorieGoal(goal)
    suspend fun setMacroSplit(split: MacroSplit) = prefs.setMacroSplit(split)
}
