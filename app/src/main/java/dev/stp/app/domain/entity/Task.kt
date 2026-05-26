package dev.stp.app.domain.entity

import enums.ProgressStatus
import enums.SyncStatus
import java.util.UUID

data class Task(
    val id: UUID,
    val userId: UUID?,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val deadline: Long,
    val progressStatus: ProgressStatus,
    val syncStatus: SyncStatus
)