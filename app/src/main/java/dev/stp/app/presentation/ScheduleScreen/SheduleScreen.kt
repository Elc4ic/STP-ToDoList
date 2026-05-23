@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.ScheduleScreen

import dev.stp.app.presentation.components.MyCalendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.R
import dev.stp.app.presentation.components.ScheduleTaskCard
import dev.stp.app.presentation.components.SwitchPanel
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

private val WeekDays = listOf(
    "SUN",
    "MON",
    "TUE",
    "WED",
    "THU",
    "FRI",
    "SAT"
)

enum class DayType {
    WEEKDAY,
    SUNDAY,
    OTHER_MONTH
}

private val MonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    vm: ScheduleViewModel = koinViewModel(),
    onTaskScreen: () -> Unit,
    notifyClick: () -> Unit,
    settingsClick: () -> Unit
) {
    val state by vm.state.collectAsState()
    val currMonth = state.displayedMonth
    val startPage = 1
    val daysTask = state.tasksDay
    val pageState = rememberPagerState(
        initialPage = startPage,
        pageCount = { 500 }
    )

    LaunchedEffect(pageState.currentPage) {
        val monthOffset = pageState.currentPage - startPage
        vm.processCommands(ScheduleCommand.ToMonth(monthOffset))
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Icon(
                        modifier = Modifier.padding(start = 10.dp),
                        painter = painterResource(R.drawable.ic_on_time),
                        contentDescription = "on time",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = notifyClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    IconButton(onClick = settingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                }
            )
        },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            SwitchPanel(onTaskScreen = onTaskScreen)
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = state.displayedMonth.format(MonthFormatter).uppercase(),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.W700,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeekDays.forEachIndexed { index, day ->
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontWeight = FontWeight.W800,
                            fontSize = 14.sp,
                            maxLines = 1,
                            softWrap = false,
                            color = if (index == 0) Color.Red else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    state = pageState
                ) {
                    MyCalendar(
                        currMonth = currMonth,
                        selectedStartDate = state.selectedStartDate,
                        selectedEndDate = state.selectedEndDate,
                        onDayClick = { vm.processCommands(ScheduleCommand.SelectDate(it)) }
                    )
                }

                Spacer(modifier = Modifier.heightIn(40.dp))

                Text(
                    text = "Tasks",
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.heightIn(12.dp))

                if (daysTask.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = MaterialTheme.colorScheme.onSecondary)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You Didn’t Have Any tasks.",
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(daysTask) { task ->
                            ScheduleTaskCard(task = task)
                        }
                    }
                }
            }
        }
    }
}



