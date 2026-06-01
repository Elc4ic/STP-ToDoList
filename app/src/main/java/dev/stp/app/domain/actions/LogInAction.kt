package dev.stp.app.domain.actions

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import dev.stp.app.domain.repository.AuthRepository
import dto.UserDto
import errors.IError


context(repo: AuthRepository)
suspend fun logIn(login: String, password: String): Either<IError, UserDto> = either {
    ensure(login.isNotBlank()) { IError.Auth.LoginFieldEmpty() }
    ensure(password.isNotBlank()) { IError.Auth.PasswordFieldEmpty() }

    repo.login(login, password).bind()
}