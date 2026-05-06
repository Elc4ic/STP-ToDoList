package ru.dedmos.todo.presentation.AddTaskScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.usecases.AddTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AddScreenState {
    data object Loading : AddScreenState
    data class Creation(
        val title: String = "",
        val content: String = "",
        val createdAt: Long = 0,
        val deadline: Long = 0
    ) : AddScreenState {
        val isSaveEnabled: Boolean
            get() = title.isNotBlank() && createdAt != 0L && deadline != 0L && deadline > createdAt
    }
}

sealed interface AddCommands {
    data class InputTitle(val title: String) : AddCommands
    data class InputContent(val content: String) : AddCommands
    data class InputTimeStart(val timeStart: Long) : AddCommands
    data class InputTimeEnd(val timeEnd: Long) : AddCommands
    data object Save : AddCommands
    data object Back : AddCommands
}

sealed interface AddScreenEvent {
    data object Finish : AddScreenEvent
}

class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AddScreenState>(AddScreenState.Creation())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddScreenEvent>()
    val event = _event.asSharedFlow()

    fun processCommand(command: AddCommands) {
        when (command) {
            is AddCommands.InputTitle -> updateCreation { it.copy(title = command.title) }
            is AddCommands.InputContent -> updateCreation { it.copy(content = command.content) }
            is AddCommands.InputTimeStart -> updateCreation { it.copy(createdAt = command.timeStart) }
            is AddCommands.InputTimeEnd -> updateCreation { it.copy(deadline = command.timeEnd) }
            AddCommands.Back -> viewModelScope.launch { _event.emit(AddScreenEvent.Finish) }
            AddCommands.Save -> saveTask()
        }
    }

    private inline fun updateCreation(crossinline update: (AddScreenState.Creation) -> AddScreenState.Creation) {
        _state.update { prevState ->
            if (prevState is AddScreenState.Creation) update(prevState) else prevState
        }
    }

    private fun saveTask() {
        val currentState = _state.value
        if (currentState !is AddScreenState.Creation || !currentState.isSaveEnabled) return

        viewModelScope.launch {
            _state.value = AddScreenState.Loading
            addTaskUseCase(
                title = currentState.title,
                content = currentState.content,
                isPinned = false,
                createdAt = currentState.createdAt,
                deadline = currentState.deadline
            )
            _event.emit(AddScreenEvent.Finish)
        }
    }
}