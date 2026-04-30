package dev.stp.app.domain.usecases

import dev.stp.app.domain.repository.TaskRepository


class SwitchPinnedUseCase (
    private val repository: TaskRepository
) {
    operator fun invoke(taskId:Int){
        repository.switchPinned(taskId)
    }
}