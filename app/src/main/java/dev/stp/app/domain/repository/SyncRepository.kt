package dev.stp.app.domain.repository

import androidx.work.ListenableWorker.Result

interface SyncRepository {
    suspend fun trySync()
}