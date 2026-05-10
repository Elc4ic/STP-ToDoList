package dev.stp.app.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.stp.app.presentation.LogInScreen.LogInSheetContent
import dev.stp.app.presentation.RegistrationScreen.RegistrationSheetContent
import enums.AuthMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    onDismiss: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondary,
    ) {
        when (mode) {
            AuthMode.LOGIN -> {
                LogInSheetContent(
                    onDismiss = onDismiss,
                    toRegistration = { mode = AuthMode.REGISTER }
                )
            }

            AuthMode.REGISTER -> {
                RegistrationSheetContent(
                    onBack = { mode = AuthMode.LOGIN },
                    onSuccess = onDismiss
                )
            }
        }
    }
}