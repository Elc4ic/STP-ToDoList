package dev.stp.app.domain.repository

import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.domain.entity.Task
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {

    suspend fun addTask(
        title: String,
        content: String,
        isPinned: Boolean,
        createdAt: Long,
        deadline: Long
    )

    suspend fun deleteTask(taskId: UUID)

    suspend fun editTask(task: Task)

    fun getAllTasks(): Flow<List<Task>>
    suspend fun getAllNotSyncTasks(): List<Task>

    suspend fun getTask(taskId: UUID): Task

    fun searchTask(query: String): Flow<List<Task>>

    suspend fun switchPinned(taskId: UUID)

   fun getTasksForPeriod(startDay: Long, endDay: Long): Flow<List<Task>>
}