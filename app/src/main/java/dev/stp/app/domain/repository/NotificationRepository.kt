package dev.stp.app.domain.repository

import dev.stp.app.domain.entity.Task
import java.util.UUID

interface NotificationRepository {
    fun scheduleDeadlineNotification(taskId: UUID, title: String, deadline: Long)
    fun cancelDeadlineNotification(taskId: UUID)
}