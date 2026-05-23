package com.graphiteplate.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single logged food item. Calories and macronutrients are stored as
 * doubles to preserve fractional input from user-typed values; rounding
 * happens only at the UI layer.
 *
 * [dayKey] is the local-date the entry belongs to in `yyyy-MM-dd` form.
 * Storing it pre-formatted lets queries group by day with a simple
 * equality match instead of date arithmetic in SQL, which keeps the
 * Room layer dialect-agnostic.
 */
@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "calories") val calories: Double,
    @ColumnInfo(name = "protein_g") val proteinG: Double,
    @ColumnInfo(name = "carbs_g") val carbsG: Double,
    @ColumnInfo(name = "fat_g") val fatG: Double,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo(name = "day_key") val dayKey: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "day_key") val dayKey: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
