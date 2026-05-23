package dev.stp.app.presentation.EditTaskScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface EditCommands {
    data class InputTitle(val title: String) : EditCommands
    data class InputContent(val content: String) : EditCommands
    data class InputTimeStart(val timeStart: Long) : EditCommands
    data class InputTimeEnd(val timeEnd: Long) : EditCommands
    data object SwitchPinned : EditCommands
    data object DeleteTask : EditCommands
    data object Save : EditCommands
    data object Back : EditCommands
    data object Retry : EditCommands
}

sealed interface EditScreenState {
    data object Loading : EditScreenState
    data class Editing(val task: Task) : EditScreenState {
        val isSaveEnabled: Boolean
            get() {
                return (task.title.isNotBlank() && task.createdAt != 0L && task.deadline != 0L && task.deadline > task.createdAt)
            }
    }

    data class Error(val message: String) : EditScreenState
}

sealed interface EditScreenEvent {
    data object Finish : EditScreenEvent
}

class EditTaskViewModel(
    private val taskId: UUID,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow<EditScreenState>(EditScreenState.Loading)

    private val _event = MutableSharedFlow<EditScreenEvent>()
    val event = _event.asSharedFlow()

    val state = _state.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        with(taskRepository) {
            viewModelScope.launch {
                _state.value = getTask(taskId).fold(
                    ifLeft = { error -> EditScreenState.Error(error.message) },
                    ifRight = { task -> EditScreenState.Editing(task) }
                )

            }
        }
    }

    fun processCommands(command: EditCommands) {
        when (command) {
            is EditCommands.InputTitle -> updateTaskState { it.copy(title = command.title) }
            is EditCommands.InputContent -> updateTaskState { it.copy(content = command.content) }
            is EditCommands.InputTimeStart -> updateTaskState { it.copy(createdAt = command.timeStart) }
            is EditCommands.InputTimeEnd -> updateTaskState { it.copy(deadline = command.timeEnd) }
            EditCommands.SwitchPinned -> updateTaskState { it.copy(isPinned = !it.isPinned) }
            EditCommands.Back -> viewModelScope.launch { _event.emit(EditScreenEvent.Finish) }
            EditCommands.Save -> saveTask()
            EditCommands.DeleteTask -> deleteTask()
            EditCommands.Retry -> loadTasks()
        }
    }

    private inline fun updateTaskState(crossinline update: (Task) -> Task) {
        _state.update { prevState ->
            if (prevState is EditScreenState.Editing) {
                prevState.copy(task = update(prevState.task))
            } else prevState
        }
    }

    private fun saveTask() {
        with(taskRepository) {
            val currentState = _state.value
            if (currentState !is EditScreenState.Editing || !currentState.isSaveEnabled) return

            viewModelScope.launch {
                _state.value = EditScreenState.Loading
                editTask(currentState.task)
                _event.emit(EditScreenEvent.Finish)
            }
        }

    }

    private fun deleteTask() {
        with(taskRepository) {
            val currentState = _state.value
            if (currentState !is EditScreenState.Editing) return

            viewModelScope.launch {
                _state.value = EditScreenState.Loading
                deleteTask(currentState.task.id)
                _event.emit(EditScreenEvent.Finish)
            }
        }
    }
}

