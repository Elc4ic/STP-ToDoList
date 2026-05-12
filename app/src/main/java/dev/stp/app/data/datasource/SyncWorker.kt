package dev.stp.app.data.datasource

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import apiRoutes.Api
import dev.stp.app.data.mapper.toDto
import dev.stp.app.domain.repository.SyncRepository
import dev.stp.app.domain.repository.TaskRepository
import dto.SyncRequest
import dto.SyncResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: TaskRepository,
    private val syncRepository: SyncRepository,
    private val client: HttpClient
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val tasks = repository.getAllNotSyncTasks()
            val response = client.post(Api.Tasks.Sync.url()) {
                setBody(SyncRequest(tasks.toDto()))
            }
            val data = response.body<SyncResponse>()
            syncRepository.processSyncResponse(data)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}