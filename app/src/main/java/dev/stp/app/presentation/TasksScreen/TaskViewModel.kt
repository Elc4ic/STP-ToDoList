@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.stp.app.presentation.TasksScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import dev.stp.app.domain.actions.getVisibleTask
import dev.stp.app.presentation.components.TaskFilter
import enums.ProgressStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface TasksCommands {
    data class InputQuery(val query: String) : TasksCommands
    data class SwitchPinned(val taskId: UUID) : TasksCommands
    data class ChangeStatus(val taskId: UUID, val status: ProgressStatus) : TasksCommands
    data class SelectFilter(val filter: TaskFilter) : TasksCommands
}

data class ScreenState(
    val query: String = "",
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val pinnedTasks: List<Task> = listOf(),
    val tasks: List<Task> = listOf(),
    val generalError: String? = null
)

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(TaskFilter.ALL)
    private val _state = MutableStateFlow(ScreenState())
    val state = _state.asStateFlow()

    init {
        with(taskRepository) {
            combine(
                query,
                selectedFilter
            ) { q, filter ->
                _state.update { it.copy(query = q, selectedFilter = filter) }
                q to filter
            }
                .flatMapLatest { (q, filter) ->
                    if (q.isBlank()) getVisibleTask()
                    else searchTask(q)
                }
                .onEach { either ->
                    either.fold(
                        ifLeft = { error ->
                            _state.update {
                                it.copy(
                                    generalError = error.message,
                                    pinnedTasks = emptyList(),
                                    tasks = emptyList()
                                )
                            }
                        },
                        ifRight = { tasks ->
                            tasks.progressFilter().let { filteredTask ->
                                val pinned = filteredTask.filter { it.isPinned }
                                val unpinned = filteredTask.filter { !it.isPinned }
                                _state.update {
                                    it.copy(
                                        pinnedTasks = pinned,
                                        tasks = unpinned,
                                        generalError = null
                                    )
                                }
                            }
                        }
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    fun List<Task>.progressFilter(): List<Task> {
        return when (selectedFilter.value) {
            TaskFilter.ALL -> this
            TaskFilter.IN_PROGRESS -> this.filter { it.progressStatus == ProgressStatus.IN_PROGRESS }
            TaskFilter.COMPLETE -> this.filter { it.progressStatus == ProgressStatus.COMPLETE }
            TaskFilter.OVERDUE -> this.filter { it.progressStatus == ProgressStatus.OVERDUE }
            TaskFilter.PINNED -> this.filter { it.isPinned }
        }
    }

    fun processCommand(command: TasksCommands) {
        with(taskRepository) {
            viewModelScope.launch {
                when (command) {
                    is TasksCommands.InputQuery -> {
                        query.update { command.query.trim() }
                    }

                    is TasksCommands.SwitchPinned -> {
                        switchPinned(command.taskId)
                    }

                    is TasksCommands.ChangeStatus -> {
                        changeProgress(command.taskId, command.status)
                    }

                    is TasksCommands.SelectFilter -> {
                        selectedFilter.update { command.filter }
                    }
                }
            }
        }
    }
}