package dev.stp.app.domain.usecases

import dev.stp.app.domain.repository.TaskRepository


class AddTaskUseCase (
    private val repository: TaskRepository
) {
    suspend operator fun invoke(title: String, content: String, isPinned: Boolean, createdAt: Long,deadline: Long){
        repository.addTask(title= title,content= content,isPinned= isPinned,createdAt= createdAt,deadline = deadline)
    }
}