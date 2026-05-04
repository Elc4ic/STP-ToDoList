@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.stp.app.presentation.TasksScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.usecases.GetAllTaskUseCase
import dev.stp.app.domain.usecases.SearchTaskUseCase
import dev.stp.app.domain.usecases.SwitchPinnedUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class TaskViewModel(
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val switchPinnedUseCase: SwitchPinnedUseCase,
    private val searchTaskUseCase: SearchTaskUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(ScreenState())

    val state = _state.asStateFlow()

    init {
        query
            .onEach { input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllTaskUseCase()
                } else {
                    searchTaskUseCase(it)
                }

            }
            .onEach { tasks ->
                val pinnedTask = tasks.filter { it.isPinned }
                val tasks = tasks.filter { !it.isPinned }
                _state.update { it.copy(pinnedTasks = pinnedTask, tasks = tasks) }

            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: TasksCommands) {
        viewModelScope.launch {
            when (command) {
                is TasksCommands.InputQuery -> {
                    query.update { command.query.trim() }

                }

                is TasksCommands.SwitchPinned -> {
                    switchPinnedUseCase(command.taskId)

                }
            }
        }
    }
}

sealed interface TasksCommands {
    data class InputQuery(val query: String) : TasksCommands
    data class SwitchPinned(val taskId: UUID) : TasksCommands

}

data class ScreenState(
    val query: String = "",
    val pinnedTasks: List<Task> = listOf(),
    val tasks: List<Task> = listOf()
)