package com.crowncode.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crowncode.data.local.dao.AnalysisHistoryDao
import com.crowncode.data.local.entity.AnalysisHistoryEntity

@Database(
    entities = [AnalysisHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CrownCodeDatabase : RoomDatabase() {
    abstract fun analysisHistoryDao(): AnalysisHistoryDao
}
