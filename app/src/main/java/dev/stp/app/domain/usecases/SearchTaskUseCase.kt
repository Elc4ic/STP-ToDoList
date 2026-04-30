package dev.stp.app.domain.usecases
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class SearchTaskUseCase (
    private val repository: TaskRepository
) {
    operator fun invoke(query: String) : Flow<List<Task>>{
        return repository.searchTask(query)
    }
}