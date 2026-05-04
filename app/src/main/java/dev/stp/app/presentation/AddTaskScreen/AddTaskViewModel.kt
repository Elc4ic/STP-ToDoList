package ru.dedmos.todo.presentation.AddTaskScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dev.stp.app.domain.usecases.AddTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dedmos.todo.presentation.AddTaskScreen.AddTaskState.Creation


class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AddTaskState>(AddTaskState.Creation())
    val state = _state.asStateFlow()


    fun processCommand(commands: Commands) {
        when (commands) {
            is Commands.InputContent -> {
                _state.update { prevState ->
                    if (prevState is Creation) {
                        prevState.copy(content = commands.content)
                    } else {
                        prevState
                    }

                }
            }

            is Commands.InputTitle -> {
                _state.update { prevState ->
                    if (prevState is Creation) {
                        prevState.copy(title = commands.title)
                    } else {
                        prevState
                    }
                }
            }

            Commands.Save -> {
                viewModelScope.launch {
                    _state.update { prevState ->
                        if (prevState is Creation) {
                            addTaskUseCase(
                                title = prevState.title,
                                content = prevState.content,
                                isPinned = false,
                                createdAt = prevState.createdAt,
                                deadline = prevState.deadline
                            )
                            prevState
                        } else {
                            prevState
                        }
                    }
                }
            }

            is Commands.InputTimeEnd -> {
                _state.update { prevState ->
                    if (prevState is Creation) {
                        prevState.copy(deadline = commands.timeEnd)
                    } else {
                        prevState
                    }
                }
            }

            is Commands.InputTimeStart -> {
                _state.update { prevState ->
                    if (prevState is Creation) {
                        prevState.copy(createdAt = commands.timeStart)
                    } else {
                        prevState
                    }
                }

            }
        }
    }
}


sealed interface AddTaskState {

    data class Creation(
        val title: String = "",
        val content: String = "",
        val createdAt: Long = 0,
        val deadline: Long = 0
    ) : AddTaskState {
        val isSaveEnabled: Boolean
            get() {
                return (title.isNotBlank() && createdAt != 0L && deadline != 0L && deadline > createdAt)
            }

    }

}

sealed interface Commands {

    data class InputTitle(val title: String) : Commands
    data class InputTimeStart(val timeStart: Long) : Commands
    data class InputTimeEnd(val timeEnd: Long) : Commands
    data class InputContent(val content: String) : Commands

    data object Save : Commands
}



