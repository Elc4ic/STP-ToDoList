package dev.stp.app.domain.usecases

import dev.stp.app.domain.repository.TaskRepository
import java.util.UUID


class SwitchPinnedUseCase (
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: UUID){
        repository.switchPinned(taskId)
    }
}