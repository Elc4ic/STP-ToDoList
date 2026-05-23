package dev.stp.app.presentation.ScheduleScreen

import android.os.Build
import androidx.annotation.RequiresApi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.usecases.GetDayTasks
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId


data class ScheduleState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val displayedMonth: YearMonth = YearMonth.now(),
    val selectedDate: Long = System.currentTimeMillis(),
    val tasksDay: List<Task> = listOf()
)

sealed interface ScheduleCommand{
    data class getTaskOnDay(val timestamp: Long): ScheduleCommand

    data class toMonth(val step: Int) : ScheduleCommand

}

class ScheduleViewModel(

    private val getDayTasks: GetDayTasks
): ViewModel(){
    @RequiresApi(Build.VERSION_CODES.O)
    private val _state = MutableStateFlow<ScheduleState>(ScheduleState())
    val state = _state.asStateFlow()

    private var dayTasksJob: Job? = null


    fun processCommands(command: ScheduleCommand){
        when(command) {
            is ScheduleCommand.getTaskOnDay -> {
                val localDate = Instant
                    .ofEpochMilli(command.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                val startDay = localDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val endDay = localDate
                    .plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() - 1
                dayTasksJob?.cancel()

                dayTasksJob =  viewModelScope.launch {

                    getDayTasks(startDay, endDay).collect{ tasks ->

                        _state.update { prevState ->

                            prevState.copy(
                                tasksDay = tasks,
                                selectedDate = command.timestamp
                            )
                        }
                    }
                }
            }

            is ScheduleCommand.toMonth -> {
                _state.update {prevState->
                    val prevDate = prevState.displayedMonth
                    prevState.copy(displayedMonth = YearMonth.now()
                        .plusMonths(command.step.toLong()))
                }

            }
        }

    }
}

