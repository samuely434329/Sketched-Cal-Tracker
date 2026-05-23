package com.graphiteplate.data.local

import com.graphiteplate.domain.model.DAY_KEY_FORMATTER
import com.graphiteplate.domain.model.DailyTotal
import com.graphiteplate.domain.model.FoodEntry
import com.graphiteplate.domain.model.MealType
import com.graphiteplate.domain.model.WeightEntry
import java.time.LocalDate

internal fun FoodEntryEntity.toDomain(): FoodEntry = FoodEntry(
    id = id,
    name = name,
    calories = calories,
    proteinG = proteinG,
    carbsG = carbsG,
    fatG = fatG,
    mealType = MealType.fromStorage(mealType),
    date = LocalDate.parse(dayKey, DAY_KEY_FORMATTER),
    createdAt = createdAt,
)

internal fun FoodEntry.toEntity(): FoodEntryEntity = FoodEntryEntity(
    id = id,
    name = name,
    calories = calories,
    proteinG = proteinG,
    carbsG = carbsG,
    fatG = fatG,
    mealType = mealType.name,
    dayKey = date.format(DAY_KEY_FORMATTER),
    createdAt = createdAt,
)

internal fun WeightEntryEntity.toDomain(): WeightEntry = WeightEntry(
    id = id,
    weightKg = weightKg,
    date = LocalDate.parse(dayKey, DAY_KEY_FORMATTER),
    createdAt = createdAt,
)

internal fun WeightEntry.toEntity(): WeightEntryEntity = WeightEntryEntity(
    id = id,
    weightKg = weightKg,
    dayKey = date.format(DAY_KEY_FORMATTER),
    createdAt = createdAt,
)

internal fun DailyTotalRow.toDomain(): DailyTotal = DailyTotal(
    date = LocalDate.parse(dayKey, DAY_KEY_FORMATTER),
    calories = calories,
    proteinG = proteinG,
    carbsG = carbsG,
    fatG = fatG,
)
