package dev.stp.app.domain.repository

import arrow.core.Either
import dev.stp.app.domain.entity.Task
import dto.SyncResponse
import errors.AppError

interface SyncRepository {
    suspend fun trySync()
    suspend fun processSyncResponse(response: SyncResponse)
    suspend fun getFromServer(): Either<AppError, Unit>
}