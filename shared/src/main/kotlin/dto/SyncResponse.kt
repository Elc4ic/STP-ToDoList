package dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val syncTasks: List<SyncTaskResponse>
)
@Serializable
data class SyncTaskResponse(
    val id: String?,
    val status: String
)
