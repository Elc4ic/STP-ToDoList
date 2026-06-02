package dev.stp.app.data.datasource

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.stp.app.MainActivity
import dev.stp.app.R

class DeadlineNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getInt("taskId", -1)
        val title = inputData.getString("title") ?: "Напоминание!"

        if (taskId == -1) return Result.failure()

        showNotification(
            context = applicationContext,
            taskId = taskId,
            title = "Скоро дедлайн!",
            message = "Задача «$title» скоро должна быть выполнена."
        )
        return Result.success()
    }

    fun showNotification(context: Context, taskId: Int, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_deadline_channel"

        val channel = NotificationChannel(
            channelId,
            "Дедлайны задач",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Напоминания о приближающихся дедлайнах"
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TASK_ID", taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId, notification)
    }
}