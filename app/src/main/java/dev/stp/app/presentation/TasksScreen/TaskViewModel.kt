@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.stp.app.presentation.TasksScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import dev.stp.app.domain.actions.getVisibleTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface TasksCommands {
    data class InputQuery(val query: String) : TasksCommands
    data class SwitchPinned(val taskId: UUID) : TasksCommands
}

data class ScreenState(
    val query: String = "",
    val pinnedTasks: List<Task> = listOf(),
    val tasks: List<Task> = listOf(),
    val generalError: String? = null
)

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(ScreenState())
    val state = _state.asStateFlow()

    init {
        with(taskRepository) {
            query
                .onEach { input ->
                    _state.update { it.copy(query = input) }
                }
                .flatMapLatest { input ->
                    if (input.isBlank()) getVisibleTask()
                    else searchTask(input)
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
                            val pinned = tasks.filter { it.isPinned }
                            val unpinned = tasks.filter { !it.isPinned }
                            _state.update {
                                it.copy(
                                    pinnedTasks = pinned,
                                    tasks = unpinned,
                                    generalError = null
                                )
                            }
                        }
                    )
                }
                .launchIn(viewModelScope)
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
                }
            }
        }

    }
}