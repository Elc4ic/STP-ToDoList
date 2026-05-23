package dev.stp.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.presentation.ScheduleScreen.DayType
import java.time.Instant
import java.time.ZoneId

@Composable
fun ScheduleDate(
    modifier: Modifier = Modifier,
    date: Long,
    selectedData: Long,
    mod: DayType = DayType.WEEKDAY,
    onDayClick: (Long) -> Unit
) {


    val backgroundColor =
        if (selectedData == date)
            MaterialTheme.colorScheme.primary
        else
            Color.Transparent

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable {
                onDayClick(date)
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = Instant
                .ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .dayOfMonth
                .toString(),

            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,

            color = when (mod) {
                DayType.WEEKDAY ->  MaterialTheme.colorScheme.onPrimary
                DayType.SUNDAY -> Color.Red
                DayType.OTHER_MONTH -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}