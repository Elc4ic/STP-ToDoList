package dev.stp.app.domain.usecases

import dev.stp.app.domain.repository.AuthRepository
import dto.UserDto
import errors.AppError


class LogInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String): Result<UserDto> {
        if (login.isBlank()) return AppError.Auth.Client.LoginFieldEmpty().toResult()
        if (password.isBlank()) return AppError.Auth.Client.PasswordFieldEmpty().toResult()
        return repository.login(login, password)
    }
}