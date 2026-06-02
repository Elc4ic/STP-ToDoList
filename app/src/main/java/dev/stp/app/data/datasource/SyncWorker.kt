package dev.stp.app.data.datasource

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import apiRoutes.Api
import arrow.core.Either
import arrow.core.raise.either
import dev.stp.app.data.mapper.toDto
import dev.stp.app.domain.repository.SyncRepository
import dev.stp.app.domain.repository.TaskRepository
import dto.SyncRequest
import dto.SyncResponse
import errors.IError
import errors.safeRequest
import io.ktor.client.HttpClient
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
        val outcome: Either<IError, Unit> = either {
            val tasks = repository.getAllNotSyncTasks().bind()

            val response = client.safeRequest<SyncResponse>(
                call = {
                    post(Api.Tasks.Sync.url()) {
                        setBody(SyncRequest(tasks.toDto()))
                    }
                }
            ).bind()

            syncRepository.processSyncResponse(response)
        }

        return outcome.fold(
            ifLeft = { error ->
                when (error) {
                    is IError.NetworkError -> Result.retry()
                    else -> Result.failure()
                }
            },
            ifRight = {
                Result.success()
            }
        )
    }
}