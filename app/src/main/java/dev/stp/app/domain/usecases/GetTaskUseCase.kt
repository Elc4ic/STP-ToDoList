package dev.stp.app.domain.usecases
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository


class GetTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId:Int) : Task {
        return repository.getTask(taskId)
    }
}