package dev.stp.app.data.mapper


import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.domain.entity.Task
import dto.TaskDto
import enums.SyncStatus

fun Task.toDbModel() =
    TaskDbModel(id, title, content, isPinned, createdAt, deadline, SyncStatus.valueOf(syncStatus))

fun Task.toDto()=
    TaskDto(id, null,title, content, isPinned, createdAt, deadline, syncStatus)

fun List<Task>.toDto(): List<TaskDto> {
    return map { it.toDto() }
}


fun TaskDbModel.toEntity() =
    Task(id, title, content, isPinned, createdAt, deadline, syncStatus.name)

fun List<TaskDbModel>.toEntity(): List<Task> {
    return map { it.toEntity() }
}

fun TaskDbModel.toDto() =
    TaskDto(id, null, title, content, isPinned, createdAt, deadline, syncStatus = syncStatus.name)

fun List<TaskDbModel>.toDto(): List<TaskDto> {
    return map { it.toDto() }
}



fun TaskDto.toDbModel(newStatus: SyncStatus) =
    TaskDbModel(id, title, content, isPinned, createdAt, deadline, newStatus)
