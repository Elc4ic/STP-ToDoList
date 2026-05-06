package dev.stp.app.presentation.RegistrationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.usecases.RegistrationUseCase
import errors.AppError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface RegistrationState {
    data object Loading : RegistrationState

    data class Content(
        val login: String = "",
        val loginError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val generalError: String? = null,
        val isSubmitting: Boolean = false
    ) : RegistrationState {
        val isSubmitEnabled: Boolean
            get() = login.isNotBlank() && password.isNotBlank() && !isSubmitting
    }
}

sealed interface RegistrationCommands {
    data class InputLogin(val value: String) : RegistrationCommands
    data class InputPassword(val value: String) : RegistrationCommands
    data object Submit : RegistrationCommands
}

sealed interface RegistrationEvent {
    data object NavigateToHome : RegistrationEvent
}

class RegistrationViewModel(
    private val regUseCase: RegistrationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RegistrationState>(RegistrationState.Content())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<RegistrationEvent>()
    val event = _event.asSharedFlow()

    fun process(command: RegistrationCommands) {
        when (command) {
            is RegistrationCommands.InputLogin -> updateContent {
                it.copy(
                    login = command.value,
                    loginError = null
                )
            }

            is RegistrationCommands.InputPassword -> updateContent {
                it.copy(
                    password = command.value,
                    passwordError = null
                )
            }

            RegistrationCommands.Submit -> registrate()
        }
    }

    private inline fun updateContent(crossinline update: (RegistrationState.Content) -> RegistrationState.Content) {
        _state.update { prevState ->
            if (prevState is RegistrationState.Content) update(prevState) else prevState
        }
    }

    private fun registrate() {
        val currentState = _state.value as? RegistrationState.Content ?: return
        if (!currentState.isSubmitEnabled) return

        viewModelScope.launch {
            updateContent { it.copy(isSubmitting = true, generalError = null) }

            val result = regUseCase(currentState.login, currentState.password)

            result.onSuccess {
                _event.emit(RegistrationEvent.NavigateToHome)
            }.onFailure { throwable ->
                val error = (throwable as? AppError)
                handleError(error)
            }

            updateContent { it.copy(isSubmitting = false) }
        }
    }

    private fun handleError(error: AppError?) {
        updateContent { prevState ->
            when (error) {
                is AppError.Auth.LoginFieldEmpty -> prevState.copy(loginError = error.message)
                is AppError.Auth.PasswordFieldEmpty -> prevState.copy(loginError = error.message)
                is AppError.Auth.PasswordTooShort -> prevState.copy(passwordError = error.message)
                is AppError.Auth.UserAlreadyExists -> prevState.copy(loginError = error.message)
                is AppError.NetworkError -> prevState.copy(generalError = error.message)
                else -> prevState.copy(generalError = error?.message ?: "Неизвестная ошибка")
            }
        }
    }
}