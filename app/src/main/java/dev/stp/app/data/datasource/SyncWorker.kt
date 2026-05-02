package dev.stp.app.data.datasource

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import apiRoutes.Api
import dev.stp.app.data.mapper.toDto
import dev.stp.app.domain.repository.TaskRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: TaskRepository,
    private val client: HttpClient
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val notSyncTasks = repository.getAllNotSyncTasks().toDto()
            client.post(Api.Tasks.Sync.path) { setBody(notSyncTasks) }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}