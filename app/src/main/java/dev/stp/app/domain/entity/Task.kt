package dev.stp.app.domain.entity

data class Task(
    val id: Int,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val deadline: Long,
    val syncStatus: String
)