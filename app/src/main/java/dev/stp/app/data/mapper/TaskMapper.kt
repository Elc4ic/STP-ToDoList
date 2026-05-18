package dev.stp.app.data.mapper


import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.domain.entity.Task
import dto.TaskDto
import enums.SyncStatus
import java.util.UUID
import kotlin.uuid.Uuid

fun Task.toDbModel() =
    TaskDbModel(id, userId, title, content, isPinned, createdAt, updatedAt, deadline, syncStatus)

fun Task.toDto() =
    TaskDto(
        id.toString(),
        userId.toString(),
        title,
        content,
        isPinned,
        createdAt,
        updatedAt,
        deadline,
        syncStatus.name
    )

fun List<Task>.toDto(): List<TaskDto> {
    return map { it.toDto() }
}


fun TaskDbModel.toEntity() =
    Task(id, userId, title, content, isPinned, createdAt, updatedAt, deadline, syncStatus)

fun List<TaskDbModel>.toEntity(): List<Task> {
    return map { it.toEntity() }
}

fun TaskDbModel.toDto() =
    TaskDto(
        id.toString(),
        userId.toString(),
        title,
        content,
        isPinned,
        createdAt,
        updatedAt,
        deadline,
        syncStatus = syncStatus.name
    )


fun TaskDto.toDbModel() =
    TaskDbModel(
        UUID.fromString(id),
        UUID.fromString(userId),
        title,
        content,
        isPinned,
        createdAt,
        updatedAt,
        deadline,
        SyncStatus.valueOf(syncStatus)
    )

fun List<TaskDto>.toDbModels() = this.map { it.toDbModel() }

fun TaskDto.toTask() =
    Task(
        UUID.fromString(id),
        UUID.fromString(userId),
        title,
        content,
        isPinned,
        createdAt,
        updatedAt,
        deadline,
        SyncStatus.valueOf(syncStatus)
    )

fun List<TaskDto>.toTasks() = this.map { it.toTask() }
