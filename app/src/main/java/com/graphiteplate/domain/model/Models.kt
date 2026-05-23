package com.graphiteplate.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Standard meal categories. Order matters — used to sort sections on Home. */
enum class MealType(val display: String) {
    Breakfast("Breakfast"),
    Lunch("Lunch"),
    Dinner("Dinner"),
    Snack("Snack");

    companion object {
        fun fromStorage(raw: String): MealType =
            entries.firstOrNull { it.name == raw } ?: Snack
    }
}

/**
 * A logged item the user ate. Macros default to zero when the user
 * doesn't supply them — calories alone are still useful.
 */
data class FoodEntry(
    val id: Long,
    val name: String,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val mealType: MealType,
    val date: LocalDate,
    val createdAt: Long,
)

/** A weight reading on a given day. */
data class WeightEntry(
    val id: Long,
    val weightKg: Double,
    val date: LocalDate,
    val createdAt: Long,
)

/** Sum of calories + macros for a single day. */
data class DailyTotal(
    val date: LocalDate,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
) {
    companion object {
        val Empty = DailyTotal(LocalDate.now(), 0.0, 0.0, 0.0, 0.0)
    }
}

/** ISO date format used as the storage key for grouping entries by day. */
val DAY_KEY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
