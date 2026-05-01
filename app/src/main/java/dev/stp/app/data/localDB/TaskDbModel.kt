package dev.stp.app.data.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import enums.SyncStatus


@Entity(tableName = "tasks")
data class TaskDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: Long,
    val deadline: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING_INSERT
)