package com.crowncode.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.crowncode.data.local.entity.AnalysisHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisHistoryDao {

    @Insert
    suspend fun insert(entry: AnalysisHistoryEntity): Long

    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AnalysisHistoryEntity>>

    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 20): Flow<List<AnalysisHistoryEntity>>

    @Query("SELECT COUNT(*) FROM analysis_history")
    suspend fun count(): Int

    @Query("DELETE FROM analysis_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM analysis_history")
    suspend fun deleteAll()
}
