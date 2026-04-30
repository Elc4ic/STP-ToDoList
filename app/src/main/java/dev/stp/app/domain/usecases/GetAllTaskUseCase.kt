package dev.stp.app.domain.usecases
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow


class GetAllTaskUseCase (
    private val repository: TaskRepository
) {
    operator fun invoke() :  Flow<List<Task>>{
        return repository.getAllTasks()
    }
}