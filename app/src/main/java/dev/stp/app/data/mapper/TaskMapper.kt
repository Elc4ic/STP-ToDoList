package dev.stp.app.data.mapper


import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.domain.entity.Task
import dto.TaskDto
import enums.SyncStatus
import java.util.UUID
import kotlin.uuid.Uuid

fun Task.toDbModel() =
    TaskDbModel(id, title, content, isPinned, createdAt,updatedAt, deadline, SyncStatus.valueOf(syncStatus))

fun Task.toDto()=
    TaskDto(id.toString(), title, content, isPinned, createdAt, updatedAt, deadline, syncStatus)

fun List<Task>.toDto(): List<TaskDto> {
    return map { it.toDto() }
}


fun TaskDbModel.toEntity() =
    Task(id, title, content, isPinned, createdAt,updatedAt, deadline, syncStatus.name)

fun List<TaskDbModel>.toEntity(): List<Task> {
    return map { it.toEntity() }
}

fun TaskDbModel.toDto() =
    TaskDto(id.toString(), title, content, isPinned, createdAt,updatedAt, deadline, syncStatus = syncStatus.name)





fun TaskDto.toDbModel(newStatus: SyncStatus) =
    TaskDbModel(UUID.fromString(id), title, content, isPinned, createdAt, updatedAt, deadline, newStatus)
