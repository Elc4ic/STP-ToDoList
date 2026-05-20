package dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val userId: String?,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deadline: Long,
    val syncStatus: String
)