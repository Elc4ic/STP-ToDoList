package dev.stp.app.domain.repository

import arrow.core.Either
import dto.SyncResponse
import errors.IError

interface SyncRepository {
    suspend fun trySync()
    suspend fun processSyncResponse(response: SyncResponse)
    suspend fun getFromServer(): Either<IError, Unit>
}