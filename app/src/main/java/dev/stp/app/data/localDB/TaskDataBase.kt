package dev.stp.app.data.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.stp.app.data.mapper.SyncStatusConverter


@Database(entities = [TaskDbModel::class], version = 1, exportSchema = false)
@TypeConverters(SyncStatusConverter::class)
abstract class TaskDataBase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
}