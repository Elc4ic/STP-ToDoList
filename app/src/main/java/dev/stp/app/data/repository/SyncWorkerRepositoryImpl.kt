package dev.stp.app.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.stp.app.data.datasource.SyncWorker
import dev.stp.app.domain.repository.SyncRepository

class SyncRepositoryImpl(private val context: Context) : SyncRepository {

    override suspend fun trySync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "sync_tasks_work",
            ExistingWorkPolicy.KEEP, // может здесь replace, надо тестить
            syncRequest
        )
    }
}