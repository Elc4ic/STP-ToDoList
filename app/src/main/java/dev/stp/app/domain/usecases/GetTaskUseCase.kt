package dev.stp.app.domain.usecases
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import java.util.UUID


class GetTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: UUID) : Task {
        return repository.getTask(taskId)
    }
}