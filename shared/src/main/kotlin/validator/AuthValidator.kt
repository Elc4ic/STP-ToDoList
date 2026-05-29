package validator

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import errors.IError

object AuthValidator {
    private val forbiddenChars = setOf('@', '#', '$', '%', '^', '&', '*', '(', ')')
    fun validateLogin(login: String): Either<IError, String> = either {
        val trimmed = login.trim()
        val hasForbidden = trimmed.any { it in forbiddenChars }
        ensure(!hasForbidden) { IError.Auth.LoginHaveSpecSimbol() }
        ensure(trimmed.isNotEmpty()) { IError.Auth.LoginFieldEmpty() }
        ensure(trimmed.length >= 3) { IError.Auth.LoginTooShort() }
        ensure(trimmed.length <= 50) { IError.Auth.LoginTooLong() }
        trimmed
    }

    fun validatePassword(password: String): Either<IError, String> = either {
        ensure(password.isNotBlank()) { IError.Auth.PasswordFieldEmpty() }
        ensure(password.length >= 6) { IError.Auth.PasswordTooShort() }
        ensure(password.length <= 50) { IError.Auth.PasswordTooLong() }
        password
    }

    fun validateCredentials(login: String, password: String): Either<IError, ValidCredentials> =
        either {
            val cleanLogin = validateLogin(login).bind()
            val cleanPassword = validatePassword(password).bind()
            ValidCredentials(cleanLogin, cleanPassword)
        }
}

data class ValidCredentials(val login: String, val password: String)