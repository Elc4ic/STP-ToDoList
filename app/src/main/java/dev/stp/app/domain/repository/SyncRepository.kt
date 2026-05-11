package dev.stp.app.domain.repository

import dev.stp.app.domain.entity.Task
import dto.SyncResponse

interface SyncRepository {
    suspend fun trySync()
    suspend fun processSyncResponse(response: SyncResponse)
    suspend fun getFromServer(): Result<List<Task>>
}