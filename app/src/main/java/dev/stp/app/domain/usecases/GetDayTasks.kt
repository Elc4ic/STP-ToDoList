package dev.stp.app.domain.usecases


import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetDayTasks(
    private val repository: TaskRepository
) {

    operator fun invoke(startDay: Long, endDay: Long
    ): Flow<List<Task>> {
        return repository.getDayTasks(startDay, endDay)
    }


}