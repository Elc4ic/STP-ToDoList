package dev.stp.domain.entity

import dev.stp.infrastructure.schema.TasksTable
import dto.TaskDto
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

data class TaskEntity(
    val id: Int,
    val userId: UUID,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: Long,
    val deadline: Long
){
    fun toDto(): TaskDto = TaskDto(
        id = this.id,
        title = this.title,
        content = this.content,
        isPinned = this.isPinned,
        createdAt = this.createdAt,
        deadline = this.deadline,
        syncStatus = "SYNCHRONIZED"
    )
}

fun ResultRow.toTaskEntity() = TaskEntity(
    id = this[TasksTable.id].value,
    userId = this[TasksTable.userId].value,
    title = this[TasksTable.title],
    content = this[TasksTable.content],
    isPinned = this[TasksTable.isPinned],
    createdAt = this[TasksTable.createdAt],
    deadline = this[TasksTable.deadline]
)