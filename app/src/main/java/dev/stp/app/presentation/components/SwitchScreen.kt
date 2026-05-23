package dev.stp.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SwitchScreen(
    modifier: Modifier = Modifier,
    onSchedule: ()->Unit

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 58.dp),
        horizontalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center

        ) {
            Text(
                modifier = Modifier.clickable{
                    onSchedule()
                },
                text = "Schedule",
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(4.dp)
                )
                .background(color = MaterialTheme.colorScheme.onSurface)
                .weight(1f)
                .padding(vertical = 1.dp),
            contentAlignment = Alignment.Center

        ) {
            Text(
                text = "Tasks",
                color = MaterialTheme.colorScheme.onPrimary

            )
        }
    }
}