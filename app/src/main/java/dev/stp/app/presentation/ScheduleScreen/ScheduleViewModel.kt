package dev.stp.app.presentation.ScheduleScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.usecases.GetPeriodTasks
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId


data class ScheduleState(
    val displayedMonth: YearMonth = YearMonth.now(),
    val selectedStartDate: Long? = null,
    val selectedEndDate: Long? = null,
    val tasksDay: List<Task> = emptyList()
)

sealed interface ScheduleCommand {
    data class SelectDate(val timestamp: Long) : ScheduleCommand
    data class ToMonth(val step: Int) : ScheduleCommand
}

class ScheduleViewModel(
    private val getPeriodTasks: GetPeriodTasks
) : ViewModel() {
    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    private var tasksJob: Job? = null
    private val zoneId = ZoneId.systemDefault()

    fun processCommands(command: ScheduleCommand) {
        when (command) {
            is ScheduleCommand.SelectDate -> {
                _state.update { prevState ->
                    val start = prevState.selectedStartDate
                    val end = prevState.selectedEndDate

                    val (newStart, newEnd) = when {
                        start == null || end != null -> {
                            command.timestamp to null
                        }

                        else -> {
                            if (command.timestamp >= start) {
                                start to command.timestamp
                            } else {
                                command.timestamp to null
                            }
                        }
                    }
                    loadTasksForRange(newStart, newEnd ?: newStart)
                    prevState.copy(
                        selectedStartDate = newStart,
                        selectedEndDate = newEnd
                    )
                }
            }

            is ScheduleCommand.ToMonth -> {
                _state.update { prevState ->
                    prevState.copy(
                        displayedMonth = YearMonth.now().plusMonths(command.step.toLong())
                    )
                }
            }
        }
    }

    private fun loadTasksForRange(startMilli: Long, endMilli: Long) {
        val startOfDay = Instant.ofEpochMilli(startMilli)
            .atZone(zoneId)
            .toLocalDate()
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()

        val endOfDay = Instant.ofEpochMilli(endMilli)
            .atZone(zoneId)
            .toLocalDate()
            .atTime(LocalTime.MAX)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            getPeriodTasks(startOfDay, endOfDay).collect { tasks ->
                _state.update { it.copy(tasksDay = tasks) }
            }
        }
    }
}
