package dev.stp.app.domain.repository

import arrow.core.Either
import dev.stp.app.domain.entity.Task
import errors.IError
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {

    suspend fun addTask(
        title: String,
        content: String,
        isPinned: Boolean,
        createdAt: Long,
        deadline: Long
    ): Either<IError, Unit>

    suspend fun deleteTask(taskId: UUID): Either<IError, Unit>

    suspend fun editTask(task: Task): Either<IError, Unit>

    fun getAllTasks(): Either<IError, Flow<List<Task>>>
    suspend fun getAllNotSyncTasks(): Either<IError, List<Task>>

    suspend fun getTask(taskId: UUID): Either<IError, Task>

    fun searchTask(query: String): Flow<Either<IError, List<Task>>>

    suspend fun switchPinned(taskId: UUID): Either<IError, Unit>

    suspend fun getTasksForPeriod(startDay: Long, endDay: Long): Either<IError, Flow<List<Task>>>
}