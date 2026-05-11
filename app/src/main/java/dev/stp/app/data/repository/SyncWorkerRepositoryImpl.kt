package dev.stp.app.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.stp.app.data.datasource.SyncWorker
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.domain.repository.SyncRepository
import dto.SyncResponse
import enums.ResultCode
import enums.SyncStatus
import java.util.UUID

class SyncRepositoryImpl(
    private val context: Context,
    private val taskDao: TaskDao
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
}