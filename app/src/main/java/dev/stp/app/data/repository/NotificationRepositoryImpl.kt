package dev.stp.app.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.stp.app.data.datasource.DeadlineNotificationWorker
import dev.stp.app.domain.repository.NotificationRepository
import java.util.UUID
import java.util.concurrent.TimeUnit

class NotificationRepositoryImpl(private val context: Context) : NotificationRepository {

    override fun scheduleDeadlineNotification(taskId: UUID, title: String, deadline: Long) {
        val timeToDeadline = deadline - System.currentTimeMillis()
        val notifyTime = timeToDeadline - TimeUnit.HOURS.toMillis(12)

        if (notifyTime <= 0) return

        val workRequest = OneTimeWorkRequestBuilder<DeadlineNotificationWorker>()
            .setInitialDelay(notifyTime, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("title" to title))
            .addTag("task_${taskId}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "notify_${taskId}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun cancelDeadlineNotification(taskId: UUID) {
        WorkManager.getInstance(context).cancelAllWorkByTag("task_$taskId")
    }
}