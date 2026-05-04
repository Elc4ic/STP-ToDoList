package dev.stp.app.domain.usecases
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository

class EditTaskUseCase (
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task){
        repository.editTask(task)
    }
}