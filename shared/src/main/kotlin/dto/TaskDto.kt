package dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Int,
    val remoteId: String? = null,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: Long,
    val deadline: Long,
    val syncStatus: String
)