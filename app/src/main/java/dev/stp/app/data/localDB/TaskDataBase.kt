package dev.stp.app.data.localDB

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [TaskDbModel::class],
          version = 1, exportSchema = false)
abstract class TaskDataBase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
}