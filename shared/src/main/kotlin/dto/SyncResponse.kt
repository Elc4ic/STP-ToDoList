package dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val syncTasks: List<SyncTaskResponse>
)
@Serializable
data class SyncTaskResponse(
    val localId: Int,
    val remoteId: String?,
    val status: String
)
