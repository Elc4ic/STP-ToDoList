package dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val unsyncTasks: List<TaskDto>
)