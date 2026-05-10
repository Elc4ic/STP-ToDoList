package dev.stp.app.presentation.RegistrationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.stp.app.presentation.components.RegistrationForm
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationSheetContent(
    viewModel: RegistrationViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                RegistrationEvent.NavigateToHome -> onSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val s = state) {
            is RegistrationState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            }

            is RegistrationState.Content -> {
                RegistrationForm(
                    state = s,
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
        }
    }
}