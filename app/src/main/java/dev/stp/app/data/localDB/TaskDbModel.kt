package dev.stp.app.data.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import enums.SyncStatus
import java.util.UUID


@Entity(tableName = "tasks")
data class TaskDbModel(
    @PrimaryKey
    val id: UUID,
    val userId: UUID?,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deadline: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING_INSERT
)