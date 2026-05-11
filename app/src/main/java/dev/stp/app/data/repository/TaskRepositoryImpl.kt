package dev.stp.app.data.repository

import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.data.mapper.toDbModel
import dev.stp.app.data.mapper.toEntity
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.NotificationRepository
import dev.stp.app.domain.repository.SyncRepository
import dev.stp.app.domain.repository.TaskRepository
import enums.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID


class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val syncRepository: SyncRepository,
    private val notificationRepository: NotificationRepository,
) : TaskRepository {

    override suspend fun addTask(
        title: String,
        content: String,
        isPinned: Boolean,
        createdAt: Long,
        deadline: Long,
    ) {
        val uuid = UUID.randomUUID()
        taskDao.addTask(
            TaskDbModel(
                id = uuid,
                title = title,
                content = content,
                isPinned = isPinned,
                createdAt = createdAt,
                updatedAt = System.currentTimeMillis(),
                deadline = deadline
            )
        )
        syncRepository.trySync()
        notificationRepository
            .scheduleDeadlineNotification(uuid, title, deadline)
    }

    override suspend fun deleteTask(taskId: UUID) {
        val task = taskDao.getTask(taskId)
        taskDao.addTask(
            task.copy(
                syncStatus = SyncStatus.PENDING_DELETE
            )
        )
        notificationRepository.cancelDeadlineNotification(taskId)
    }

    override suspend fun editTask(task: Task) {
        val editTask = task.toDbModel().copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_UPDATE
        )
        taskDao.addTask(editTask)
        notificationRepository.scheduleDeadlineNotification(
            task.id,
            task.title,
            task.deadline
        )
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTask().map {
            it.toEntity()
        }
    }

    override suspend fun getAllNotSyncTasks(): List<Task> {
        return taskDao.getAllNotSyncTask().map {
            it.toEntity()
        }
    }

    override suspend fun getTask(taskId: UUID): Task {
        return taskDao.getTask(taskId).toEntity()
    }

    override fun searchTask(query: String): Flow<List<Task>> {
        return taskDao.searchTask(query).map {
            it.toEntity()
        }
    }

    override suspend fun switchPinned(taskId: UUID) {
        val task = taskDao.getTask(taskId)
        taskDao.addTask(
            task.copy(
                isPinned = !task.isPinned,
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING_UPDATE
            )
        )
        taskDao.switchPinned(taskId)
    }
}