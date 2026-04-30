package dev.stp.app.data.mapper


import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.domain.entity.Task



fun Task.toDbModel() : TaskDbModel {
    return TaskDbModel(id, title, content, isPinned,createdAt,deadline)
}

fun TaskDbModel.toEntity() : Task{
    return Task(id, title, content, isPinned,createdAt,deadline)
}

fun List<TaskDbModel>.toEntity(): List<Task>{
    return map{it.toEntity()}
}