package dev.stp.app.domain.usecases
import dev.stp.app.domain.repository.TaskRepository


class DeleteTaskUseCase (
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId:Int){
        repository.deleteTask(taskId)
    }
}