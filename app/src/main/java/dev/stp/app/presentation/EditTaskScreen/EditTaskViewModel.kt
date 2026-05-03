package dev.stp.app.presentation.EditTaskScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.usecases.DeleteTaskUseCase
import dev.stp.app.domain.usecases.EditTaskUseCase
import dev.stp.app.domain.usecases.GetTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class EditTaskViewModel(
    private val taskId: UUID,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
): ViewModel(){

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Editing())

    val state = _state.asStateFlow()

    init{
        viewModelScope.launch {
            _state.update {
                val task = getTaskUseCase(taskId)
                ScreenState.Editing(task)
            }
        }
    }

    fun processCommands(commands: EditCommands){
        when(commands) {
            EditCommands.Back -> {


            }
            is EditCommands.DeleteTask -> {
                viewModelScope.launch {
                    _state.update {prevState->
                        if(prevState is ScreenState.Editing){
                            val task =prevState.task
                            deleteTaskUseCase(task.id)
                            prevState
                        }
                        else{
                            prevState
                        }
                    }
                }


            }
            is EditCommands.InputContent -> {
                _state.update {prevState->
                    if(prevState is ScreenState.Editing){
                        val taks = prevState.task.copy(content = commands.content)
                        prevState.copy(task = taks)
                    }
                    else{
                        prevState
                    }

                }

            }
            is EditCommands.InputTitle -> {
                _state.update {prevState->
                    if(prevState is ScreenState.Editing){
                        val task = prevState.task.copy(title = commands.title)
                        prevState.copy(task = task)
                    }
                    else{
                        prevState
                    }

                }

            }
            EditCommands.Save -> {
                viewModelScope.launch {
                    _state.update {prevState->
                        if(prevState is ScreenState.Editing){
                            val task = prevState.task
                            editTaskUseCase(task)
                            prevState
                        }
                        else{
                            prevState
                        }

                    }
                }

            }
            is EditCommands.SwitchPinned -> {
                _state.update {prevState->
                    if(prevState is ScreenState.Editing){
                        val isPinned = prevState.task.isPinned
                        val task = prevState.task.copy(isPinned = !isPinned)
                        prevState.copy(task = task)
                    }
                    else{
                        prevState
                    }

                }

            }

            is EditCommands.InputTimeEnd -> {
                _state.update {prevState->
                    if(prevState is ScreenState.Editing){
                        val task = prevState.task.copy(deadline = commands.timeEnd)
                        prevState.copy(task = task)
                    }
                    else{
                        prevState
                    }

                }

            }
            is EditCommands.InputTimeStart -> {
                _state.update {prevState->
                    if(prevState is ScreenState.Editing){
                        val task = prevState.task.copy(createdAt = commands.timeStart)
                        prevState.copy(task = task)
                    }
                    else{
                        prevState
                    }

                }

            }
        }
    }











}

sealed interface EditCommands{

    data class InputTitle(val title: String): EditCommands

    data class InputContent(val content: String): EditCommands

    data class DeleteTask(val taskId: UUID): EditCommands

    data class SwitchPinned(val taskId: UUID): EditCommands

    data object Save: EditCommands

    data object  Back: EditCommands

    data class InputTimeStart(val timeStart: Long) : EditCommands
    data class InputTimeEnd(val timeEnd: Long) : EditCommands

}
sealed interface ScreenState{
    data class Editing(
        val task: Task = Task(
            id = UUID(12,1),
            title = "",
            content = "",
            isPinned = false,
            createdAt = 0L,
            updatedAt = 0L,
            deadline = 0L,
            syncStatus = ""
        )
    ) : ScreenState{
        val isSaveEnabled: Boolean
            get(){
                return  (task.title.isNotBlank() && task.createdAt != 0L && task.deadline != 0L && task.deadline > task.createdAt)
            }

    }

}

