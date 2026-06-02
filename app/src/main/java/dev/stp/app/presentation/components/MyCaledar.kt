package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.stp.app.presentation.ScheduleScreen.DayType

import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId


@Composable
fun MyCalendar(
    currMonth: YearMonth,
    selectedStartDate: Long?,
    selectedEndDate: Long?,
    onDayClick: (Long) -> Unit,
) {
    val zoneId = ZoneId.systemDefault()

    val startLocalDate = remember(selectedStartDate) {
        selectedStartDate?.let { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate() }
    }
    val endLocalDate = remember(selectedEndDate) {
        selectedEndDate?.let { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate() }
    }

    val calendarDays = remember(currMonth) {
        val firstDayOfMonth = currMonth.atDay(1)
        val offset = firstDayOfMonth.dayOfWeek.value % 7
        val startDate = firstDayOfMonth.minusDays(offset.toLong())
        List(42) { index -> startDate.plusDays(index.toLong()) }
    }

    val weeks = remember(calendarDays) { calendarDays.chunked(7) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { localDate ->
                    val timestamp = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

                    val isSelected = localDate == startLocalDate || localDate == endLocalDate
                    val isInRange =
                        startLocalDate != null && endLocalDate != null &&
                            localDate.isAfter(startLocalDate) && localDate.isBefore(endLocalDate)

                    val dayType = when {
                        localDate.month != currMonth.month -> DayType.OTHER_MONTH
                        localDate.dayOfWeek == java.time.DayOfWeek.SUNDAY -> DayType.SUNDAY
                        else -> DayType.WEEKDAY
                    }

                    ScheduleDate(
                        date = timestamp,
                        isSelected = isSelected,
                        isInRange = isInRange,
                        mod = dayType,
                        onClick = { onDayClick(timestamp) }
                    )
                }
            }
        }
    }
}
