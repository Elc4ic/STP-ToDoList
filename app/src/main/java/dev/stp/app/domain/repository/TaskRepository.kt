package dev.stp.app.domain.repository

import arrow.core.Either
import dev.stp.app.domain.entity.Task
import enums.ProgressStatus
import errors.AppError
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {

    suspend fun addTask(
        title: String,
        content: String,
        isPinned: Boolean,
        createdAt: Long,
        deadline: Long
    ): Either<AppError, Unit>

    suspend fun deleteTask(taskId: UUID): Either<AppError, Unit>

    suspend fun editTask(task: Task): Either<AppError, Unit>

    fun getAllTasks(): Either<AppError, Flow<List<Task>>>
    suspend fun getAllNotSyncTasks(): Either<AppError, List<Task>>

    suspend fun getTask(taskId: UUID): Either<AppError, Task>

    fun searchTask(query: String): Flow<Either<AppError, List<Task>>>

    suspend fun switchPinned(taskId: UUID): Either<AppError, Unit>

    suspend fun changeProgress(
        taskId: UUID,
        status: ProgressStatus
    ): Either<AppError, Unit>

    suspend fun getTasksForPeriod(startDay: Long, endDay: Long): Either<AppError, Flow<List<Task>>>
}