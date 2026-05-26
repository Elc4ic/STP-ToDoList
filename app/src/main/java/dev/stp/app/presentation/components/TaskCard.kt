package dev.stp.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.data.mapper.DateFormater
import dev.stp.app.domain.entity.Task
import dev.stp.app.presentation.ui.theme.CustomIcons
import enums.ProgressStatus
import enums.SyncStatus

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    task: Task,
    onLongClick: (Task) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .combinedClickable(
                onClick = { onTaskClick(task) },
                onLongClick = { onLongClick(task) }
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.primary)
            .height(IntrinsicSize.Max)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = task.content,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "${DateFormater.formatDateFromMillis(task.createdAt)} - ${
                        DateFormater.formatDateFromMillis(task.deadline)
                    }",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.W400,
                    fontSize = 10.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                if (task.isPinned) {
                    Icon(
                        imageVector = CustomIcons.Pinned,
                        contentDescription = "pinned",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Icon(
                    imageVector = when (task.syncStatus) {
                        SyncStatus.SYNCHRONIZED -> Icons.Default.Check
                        else -> Icons.Default.Sync
                    },
                    contentDescription = "sync",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = when (task.progressStatus) {
                    ProgressStatus.COMPLETE -> "Выполнен"
                    ProgressStatus.CANCELED -> "Отменен"
                    ProgressStatus.IN_PROGRESS -> "В процессе"
                    ProgressStatus.OVERDUE -> "Просрочен"
                },
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.W400,
                fontSize = 10.sp
            )
        }
    }
}