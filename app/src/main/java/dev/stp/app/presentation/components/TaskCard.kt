package dev.stp.app.presentation.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.data.mapper.DateFormater
import dev.stp.app.domain.entity.Task
import dev.stp.app.presentation.ui.theme.CustomIcons

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    task: Task,
    onLongClick : (Task) ->Unit
){

    Column(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick ={
                    onTaskClick(task)
                },
                onLongClick = {
                    onLongClick(task)
                }
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.primary )
            .padding(16.dp)

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Text(
                    text = "${DateFormater.formatDateFromMillis(task.createdAt)} - ${DateFormater.formatDateFromMillis(task.deadline)} ",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.W400,
                    fontSize = 10.sp
                )
            }
            if(task.isPinned){
                Icon(
                    imageVector = CustomIcons.Pinned,
                    contentDescription = "pinned",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

        }
    }
}