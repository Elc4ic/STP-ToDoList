package dev.stp.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import dev.stp.app.presentation.LogInScreen.LogInScreen
import dev.stp.app.presentation.RegistrationScreen.RegistrationScreen
import dev.stp.app.presentation.navigation.NavGraph
import dev.stp.app.presentation.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                LogInScreen(
                    onSuccess = {}
                ) { }

            }
        }
    }
}

