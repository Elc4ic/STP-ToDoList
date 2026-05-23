package dev.stp.app.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import apiRoutes.Api
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import dev.stp.app.data.datasource.SyncWorker
import dev.stp.app.data.datasource.TokenStore
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.data.mapper.safeApiCall
import dev.stp.app.data.mapper.toDbModels
import dev.stp.app.domain.repository.SyncRepository
import dto.GetTaskResponse
import dto.SyncResponse
import enums.ResultCode
import enums.SyncStatus
import errors.AppError
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import java.util.UUID

class SyncRepositoryImpl(
    private val context: Context,
    private val taskDao: TaskDao,
    private val client: HttpClient,
    private val tokenStore: TokenStore
) : SyncRepository {

    override suspend fun trySync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "sync_tasks_work",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    override suspend fun processSyncResponse(response: SyncResponse) {
        response.syncTasks.forEach { taskResponse ->
            val taskId = UUID.fromString(taskResponse.id)

            when (ResultCode.valueOf(taskResponse.status)) {
                ResultCode.SUCCESS -> {
                    val localTask = taskDao.getTask(taskId)

                    when (localTask.syncStatus) {
                        SyncStatus.PENDING_DELETE -> {
                            taskDao.deleteTask(taskId)
                        }

                        else -> {
                            taskDao.updateSyncStatus(taskId, SyncStatus.SYNCHRONIZED)
                        }
                    }
                }

                ResultCode.ERROR -> {}
            }
        }
    }

    override suspend fun getFromServer(): Either<AppError, Unit> = either {
        val userId = tokenStore.userId.first()
            .toEither { AppError.Unknown("Пользователь не авторизован") }
            .bind()

        val response = safeApiCall<GetTaskResponse>(
            call = { client.post(Api.Tasks.GetAll.url()) },
            mapError = { status ->
                if (status == HttpStatusCode.NotFound) AppError.Server.Auth.UserNotFound()
                else null
            }
        ).bind()

        val tasks = response.tasks.toDbModels()
        ensure(tasks.isNotEmpty()) { AppError.Unknown("Нет заданий") }
        syncLocalDatabaseWithServer(tasks, userId)
    }

    suspend fun syncLocalDatabaseWithServer(serverTasks: List<TaskDbModel>, userId: UUID) {
        taskDao.withTransaction {

            val pendingTaskIds = taskDao.getAllNotSyncTaskIdsByUserId(userId).toSet()

            serverTasks.forEach { serverTask ->
                if (!pendingTaskIds.contains(serverTask.id)) {
                    taskDao.addTask(serverTask.copy(syncStatus = SyncStatus.SYNCHRONIZED))
                }
            }

            val serverTaskIds = serverTasks.map { it.id }.toSet()
            val localTasks = taskDao.getAllTasksByUserId(userId)

            localTasks.forEach { localTask ->
                if (!serverTaskIds.contains(localTask.id) && localTask.syncStatus == SyncStatus.SYNCHRONIZED) {
                    taskDao.deleteTask(localTask.id)
                }
            }
        }
    }
}