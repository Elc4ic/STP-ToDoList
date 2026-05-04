package dev.stp.app.domain.usecases
import dev.stp.app.domain.repository.TaskRepository
import java.util.UUID


class DeleteTaskUseCase (
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: UUID){
        repository.deleteTask(taskId)
    }
}