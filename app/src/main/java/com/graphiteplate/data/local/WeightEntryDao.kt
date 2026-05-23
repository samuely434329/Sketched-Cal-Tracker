package com.graphiteplate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {

    @Query("SELECT * FROM weight_entries ORDER BY created_at DESC LIMIT :limit")
    fun observeRecent(limit: Int = 90): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY created_at DESC LIMIT 1")
    fun observeLatest(): Flow<WeightEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeightEntryEntity): Long

    @Delete
    suspend fun delete(entry: WeightEntryEntity)
}
