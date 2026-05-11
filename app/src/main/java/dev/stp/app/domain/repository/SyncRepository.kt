package dev.stp.app.domain.repository

import androidx.work.ListenableWorker.Result
import dto.SyncResponse

interface SyncRepository {
    suspend fun trySync()
    suspend fun processSyncResponse(response: SyncResponse)
}