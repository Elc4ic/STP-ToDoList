package dev.stp.app.presentation.navigation

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.stp.app.domain.usecases.RegistrationUseCase
import dev.stp.app.presentation.AddTaskScreen.AddTaskScreen
import dev.stp.app.presentation.LogInScreen.LogInScreen
import dev.stp.app.presentation.EditTaskScreen.EditScreen
import dev.stp.app.presentation.RegistrationScreen.RegistrationScreen
import dev.stp.app.presentation.TasksScreen.TasksScreen
import java.util.UUID


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route,
    ) {
        composable(Screen.LogIn.route) {
            LogInScreen {
                navController.navigate(Screen.Tasks.route)
            }
        }
        composable(Screen.Registration.route) {
            RegistrationScreen {
                navController.navigate(Screen.Tasks.route)
            }
        }
        composable(Screen.Tasks.route) {
            TasksScreen(
                addTaskClick = {
                    navController.navigate(Screen.AddTask.route)
                },
                settingsClick = {},
                notifyClick = {},
                onTaskClick = { task ->
                    navController.navigate(Screen.EditScreen.createRoute(task.id))
                }
            )
        }
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                onFinish = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditScreen.route) {
            val taskId = Screen.EditScreen.getTaskId(it.arguments)
            EditScreen(
                taskId = taskId,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
    }
}


sealed class Screen(val route: String) {
    data object LogIn : Screen("login")
    data object Registration : Screen("registration")
    data object Tasks : Screen("main")
    data object AddTask : Screen("add_task")

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