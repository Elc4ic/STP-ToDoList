package dev.stp.app.data.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.stp.app.data.mapper.SyncStatusConverter
import dev.stp.app.data.mapper.UuidConverter


@Database(entities = [TaskDbModel::class], version = 2, exportSchema = false)
@TypeConverters(SyncStatusConverter::class, UuidConverter::class)
abstract class TaskDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}