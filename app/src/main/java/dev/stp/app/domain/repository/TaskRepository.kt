package dev.stp.app.domain.repository

import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.domain.entity.Task
import kotlinx.coroutines.flow.Flow
interface TaskRepository {

    suspend fun addTask(title: String,
                        content: String,
                        isPinned: Boolean,
                        createdAt: Long,
                        deadline: Long)

    suspend fun deleteTask(taskId:Int)

    suspend fun editTask(task: Task)

    fun getAllTasks(): Flow<List<Task>>
    suspend fun getAllNotSyncTasks(): List<Task>

    suspend fun getTask(taskId : Int) : Task

    fun searchTask(query: String) :  Flow<List<Task>>

    fun switchPinned(taskId : Int)
}