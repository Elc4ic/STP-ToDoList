package dev.stp.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    date: Long,
    isSelected: Boolean,
    isInRange: Boolean,
    mod: DayType,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isInRange -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        mod == DayType.OTHER_MONTH -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
        mod == DayType.SUNDAY -> Color.Red
        else -> MaterialTheme.colorScheme.onPrimary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).dayOfMonth.toString(),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}