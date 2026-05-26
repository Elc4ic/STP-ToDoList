package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import enums.ProgressStatus
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeProgressStatusDialog(
    taskId: UUID,
    currentStatus: ProgressStatus,
    onDismiss: () -> Unit,
    onSelect: (UUID, ProgressStatus) -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Изменить статус",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(contentAlignment = Alignment.Center) {
                    OutlinedButton(onClick = { isMenuOpen = true }) {
                        Text(
                            text = when (selectedStatus) {
                                ProgressStatus.COMPLETE -> "Выполнен"
                                ProgressStatus.CANCELED -> "Отменен"
                                ProgressStatus.IN_PROGRESS -> "В процессе"
                                ProgressStatus.OVERDUE -> "Просрочен"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuOpen,
                        onDismissRequest = { isMenuOpen = false }
                    ) {
                        ProgressStatus.entries
                            .filter { selectedStatus != it }
                            .forEach { status ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = when (status) {
                                                ProgressStatus.COMPLETE -> "Выполнен"
                                                ProgressStatus.CANCELED -> "Отменен"
                                                ProgressStatus.IN_PROGRESS -> "В процессе"
                                                ProgressStatus.OVERDUE -> "Просрочен"
                                            }
                                        )
                                    },
                                    onClick = {
                                        selectedStatus = status
                                        isMenuOpen = false
                                    }
                                )
                            }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSelect(taskId, selectedStatus)
                        onDismiss()
                    }) {
                        Text("Применить")
                    }
                }
            }
        }
    }
}