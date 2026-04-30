package dev.stp.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.stp.app.presentation.AddTaskScreen.AddTaskScreen
import dev.stp.app.presentation.TasksScreen.TasksScreen



@Composable
fun NavGraph(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.route,
    ){
        composable(Screen.Tasks.route) {
            TasksScreen(
                addTaskClick = {
                    navController.navigate(Screen.AddTask.route)
                },
                settingsClick = {},
                notifyClick = {},
                onTaskClick = {}
            )


        }
        composable (Screen.AddTask.route){
            AddTaskScreen(
                onFinish = {
                    navController.popBackStack()
                },
                onBack = {navController.popBackStack()}
            )
        }
    }

}



sealed class Screen( val route: String){

    data object Tasks : Screen("main")
    data object AddTask: Screen("add_task")
}