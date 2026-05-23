package dev.stp.app.presentation.navigation

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.stp.app.presentation.AddTaskScreen.AddTaskScreen
import dev.stp.app.presentation.EditTaskScreen.EditScreen
import dev.stp.app.presentation.ScheduleScreen.ScheduleScreen
import dev.stp.app.presentation.TasksScreen.TasksScreen
import java.util.UUID


@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route,
    ) {
        composable(Screen.Tasks.route) {
            TasksScreen(
                addTaskClick = {
                    navController.navigate(Screen.AddTask.route)
                },
                settingsClick = {},
                notifyClick = {},
                onTaskClick = { task ->
                    navController.navigate(Screen.EditScreen.createRoute(task.id))
                },
                onSchedule = {
                    navController.navigate(Screen.ScheduleScreen.route)
                }
            )
        }
        composable(Screen.ScheduleScreen.route) {
            ScheduleScreen(
                onTaskScreen = {
                    navController.popBackStack()
                },
                notifyClick = {},
                settingsClick = {}
            )
        }

        composable(Screen.AddTask.route) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AddTaskScreen(
                    onFinish = {
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.EditScreen.route) {
            val taskId = Screen.EditScreen.getTaskId(it.arguments)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                EditScreen(
                    taskId = taskId,
                    onFinish = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


sealed class Screen(val route: String) {
    data object LogIn : Screen("login")
    data object Registration : Screen("registration")
    data object Tasks : Screen("main")
    data object AddTask : Screen("add_task")

    data object ScheduleScreen : Screen("schedule")

    data object EditScreen : Screen("edit_task/{task_id}") {
        fun createRoute(taskId: UUID): String {
            return "edit_task/$taskId"
        }

        fun getTaskId(arguments: Bundle?): UUID {
            val taskId = arguments?.getString("task_id")
            return UUID.fromString(taskId)
        }
    }
}