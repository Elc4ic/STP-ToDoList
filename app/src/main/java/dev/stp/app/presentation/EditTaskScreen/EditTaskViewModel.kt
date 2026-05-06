package dev.stp.app.presentation.EditTaskScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.usecases.DeleteTaskUseCase
import dev.stp.app.domain.usecases.EditTaskUseCase
import dev.stp.app.domain.usecases.GetTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dedmos.todo.presentation.AddTaskScreen.Commands
import java.util.UUID

class EditTaskViewModel(
    private val taskId: UUID,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)

    private val _event = MutableSharedFlow<ScreenEvent>()
    val event = _event.asSharedFlow()

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val task = getTaskUseCase(taskId)
            _state.value = ScreenState.Editing(task)
        }
    }

    fun processCommands(command: EditCommands) {
        when (command) {
            is EditCommands.InputTitle -> updateTaskState { it.copy(title = command.title) }
            is EditCommands.InputContent -> updateTaskState { it.copy(content = command.content) }
            is EditCommands.InputTimeStart -> updateTaskState { it.copy(createdAt = command.timeStart) }
            is EditCommands.InputTimeEnd -> updateTaskState { it.copy(deadline = command.timeEnd) }
            EditCommands.SwitchPinned -> updateTaskState { it.copy(isPinned = !it.isPinned) }
            EditCommands.Back -> viewModelScope.launch { _event.emit(ScreenEvent.Finish) }
            EditCommands.Save -> saveTask()
            EditCommands.DeleteTask -> deleteTask()
        }
    }

    private inline fun updateTaskState(crossinline update: (Task) -> Task) {
        _state.update { prevState ->
            if (prevState is ScreenState.Editing) {
                prevState.copy(task = update(prevState.task))
            } else prevState
        }
    }

    private fun saveTask() {
        val currentState = _state.value
        if (currentState !is ScreenState.Editing || !currentState.isSaveEnabled) return

        viewModelScope.launch {
            _state.value = ScreenState.Loading
            editTaskUseCase(currentState.task)
            _event.emit(ScreenEvent.Finish)
        }
    }

    private fun deleteTask() {
        val currentState = _state.value
        if (currentState !is ScreenState.Editing) return

        viewModelScope.launch {
            _state.value = ScreenState.Loading
            deleteTaskUseCase(currentState.task.id)
            _event.emit(ScreenEvent.Finish)
        }
    }

    fun processCommand(commands: Commands) {
        TODO()
    }
}


sealed interface EditCommands {
    data class InputTitle(val title: String) : EditCommands
    data class InputContent(val content: String) : EditCommands
    data class InputTimeStart(val timeStart: Long) : EditCommands
    data class InputTimeEnd(val timeEnd: Long) : EditCommands
    data object SwitchPinned : EditCommands
    data object DeleteTask : EditCommands
    data object Save : EditCommands
    data object Back : EditCommands
}

sealed interface ScreenState {
    data object Loading : ScreenState
    data class Editing(val task: Task) : ScreenState {
        val isSaveEnabled: Boolean
            get() {
                return (task.title.isNotBlank() && task.createdAt != 0L && task.deadline != 0L && task.deadline > task.createdAt)
            }
    }
}

sealed interface ScreenEvent {
    data object Finish : ScreenEvent
}

