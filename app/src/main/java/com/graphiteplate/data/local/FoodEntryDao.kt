package com.graphiteplate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Query("SELECT * FROM food_entries WHERE day_key = :dayKey ORDER BY created_at ASC")
    fun observeByDay(dayKey: String): Flow<List<FoodEntryEntity>>

    @Query(
        """
        SELECT day_key AS dayKey,
               SUM(calories) AS calories,
               SUM(protein_g) AS proteinG,
               SUM(carbs_g) AS carbsG,
               SUM(fat_g) AS fatG
        FROM food_entries
        GROUP BY day_key
        ORDER BY day_key DESC
        LIMIT :limit
        """
    )
    fun observeDailyTotals(limit: Int = 60): Flow<List<DailyTotalRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodEntryEntity): Long

    @Delete
    suspend fun delete(entry: FoodEntryEntity)
}

/** Aggregate row used by [FoodEntryDao.observeDailyTotals]. */
data class DailyTotalRow(
    val dayKey: String,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
)
