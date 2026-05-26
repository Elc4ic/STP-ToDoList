package dev.stp.app.data.repository

import arrow.core.Either
import arrow.core.raise.either
import dev.stp.app.data.datasource.TokenStore
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.data.mapper.toDbModel
import dev.stp.app.data.mapper.toEntity
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.NotificationRepository
import dev.stp.app.domain.repository.SyncRepository
import dev.stp.app.domain.repository.TaskRepository
import enums.ProgressStatus
import enums.SyncStatus
import errors.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID


class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val tokenStore: TokenStore,
    private val syncRepository: SyncRepository,
    private val notificationRepository: NotificationRepository,
) : TaskRepository {

    override suspend fun addTask(
        title: String,
        content: String,
        isPinned: Boolean,
        createdAt: Long,
        deadline: Long,
    ): Either<AppError, Unit> = either {
        val userIdOption = tokenStore.userId.first()
        val userId = userIdOption.getOrNull()

        val uuid = UUID.randomUUID()
        Either.catch {
            taskDao.addTask(
                TaskDbModel(
                    id = uuid,
                    userId = userId,
                    title = title,
                    content = content,
                    isPinned = isPinned,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis(),
                    deadline = deadline
                )
            )
        }.mapLeft { AppError.Client.DB.CannotSave() }.bind()
        userId?.let {
            syncRepository.trySync()
        }

        notificationRepository.scheduleDeadlineNotification(uuid, title, deadline)
    }

    override suspend fun deleteTask(taskId: UUID): Either<AppError, Unit> = either {
        val task = Either.catch { taskDao.getTask(taskId) }
            .mapLeft { AppError.Client.DB.NotFound() }
            .bind()

        Either.catch {
            taskDao.addTask(
                task.copy(
                    syncStatus = SyncStatus.PENDING_DELETE
                )
            )
        }.mapLeft { AppError.Client.DB.CannotSave() }.bind()
        notificationRepository.cancelDeadlineNotification(taskId)
    }

    override suspend fun editTask(task: Task): Either<AppError, Unit> = either {
        val editTask = task.toDbModel().copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_UPDATE
        )

        Either.catch { taskDao.addTask(editTask) }
            .mapLeft { AppError.Client.DB.WriteError(it) }.bind()
        notificationRepository.scheduleDeadlineNotification(
            task.id,
            task.title,
            task.deadline
        )
    }

    override fun getAllTasks(): Either<AppError, Flow<List<Task>>> = either {
        Either.catch {
            taskDao.getAllTask().map { it.toEntity() }
        }.mapLeft { AppError.Client.DB.WriteError(it) }.bind()
    }

    override suspend fun getAllNotSyncTasks(): Either<AppError, List<Task>> = either {
        Either.catch {
            taskDao.getAllNotSyncTask().map { it.toEntity() }
        }.mapLeft { AppError.Client.DB.WriteError(it) }.bind()
    }

    override suspend fun getTask(taskId: UUID): Either<AppError, Task> = either {
        Either.catch {
            taskDao.getTask(taskId).toEntity()
        }.mapLeft { AppError.Client.DB.WriteError(it) }.bind()
    }

    override fun searchTask(query: String): Flow<Either<AppError, List<Task>>> {
        return taskDao.searchTask(query)
            .map { dbModels ->
                val entities = dbModels.toEntity()
                Either.Right(entities) as Either<AppError, List<Task>>
            }
            .catch { throwable ->
                emit(Either.Left(AppError.Client.DB.WriteError(throwable)))
            }
    }

    override suspend fun switchPinned(taskId: UUID): Either<AppError, Unit> = either {
        val task = Either.catch { taskDao.getTask(taskId) }
            .mapLeft { AppError.Client.DB.NotFound() }
            .bind()

        Either.catch {
            taskDao.addTask(
                task.copy(
                    isPinned = !task.isPinned,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )
        }.mapLeft { AppError.Client.DB.WriteError(it) }.bind()
    }

    override suspend fun changeProgress(
        taskId: UUID,
        status: ProgressStatus
    ): Either<AppError, Unit> = either {
        val task = Either.catch { taskDao.getTask(taskId) }
            .mapLeft { AppError.Client.DB.NotFound() }
            .bind()

        Either.catch {
            taskDao.addTask(
                task.copy(
                    processStatus = status,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )
        }.mapLeft { AppError.Client.DB.WriteError(it) }.bind()
    }

    override suspend fun getTasksForPeriod(
        startDay: Long,
        endDay: Long
    ): Either<AppError, Flow<List<Task>>> = either {
        Either.catch {
            taskDao.getTasksForPeriod(startDay, endDay).map { it.toEntity() }
        }.mapLeft { AppError.Client.DB.NotFound() }.bind()
    }
}