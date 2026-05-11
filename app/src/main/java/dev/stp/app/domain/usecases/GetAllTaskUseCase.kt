package dev.stp.app.domain.usecases
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import enums.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetAllTaskUseCase (
    private val repository: TaskRepository
) {
    operator fun invoke() :  Flow<List<Task>>{
        return repository.getAllTasks().map { tasks ->
            tasks.filter { it.syncStatus != SyncStatus.PENDING_DELETE }
        }
    }
}