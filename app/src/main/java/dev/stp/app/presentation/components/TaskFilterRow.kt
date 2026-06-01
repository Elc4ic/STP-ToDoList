package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TaskFilter(val displayName: String) {
    ALL("Все"),
    IN_PROGRESS("В процессе"),
    COMPLETE("Выполненные"),
    OVERDUE("Просроченные"),
    PINNED("Закрепленные")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterRow(
    modifier: Modifier = Modifier,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TaskFilter.entries) { filter ->
            val isSelected = filter == selectedFilter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                shape = RoundedCornerShape(8.dp),
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF212121),
                    labelColor = Color.White,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}