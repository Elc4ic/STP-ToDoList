package dev.stp.app.domain.usecases

import dev.stp.app.domain.repository.AuthRepository
import dto.UserDto
import errors.AppError

class RegistrationUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String): Result<UserDto> {
        if (login.isBlank()) return AppError.Auth.LoginFieldEmpty().toResult()
        if (password.isBlank()) return AppError.Auth.PasswordFieldEmpty().toResult()
        if (password.length < 6) return AppError.Auth.PasswordTooShort().toResult()
        return repository.register(login, password)
    }
}