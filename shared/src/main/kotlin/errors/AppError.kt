package errors

sealed class AppError : Exception() {
    sealed class Auth : AppError() {
        sealed class Server : Auth() {
            class InvalidCredentials : Auth()
            class InvalidToken : Auth()
            class UserNotFound : Auth()
            data class UserAlreadyExists(val login: String) : Auth()

        }

        sealed class Client : Auth() {
            class LoginFieldEmpty : Auth()
            class PasswordFieldEmpty : Auth()
            class PasswordTooShort : Auth()
        }
    }

    sealed class Task : AppError() {
        sealed class Server : Task() {
            class CannotSaveTask : Task()
            class DatabaseError() : Task()
        }

        sealed class Client() : Task() {
            class CannotSaveTask : Task()
        }
    }

    class NetworkError : AppError()
    class ServerError : AppError()
    class Unknown(message: String) : AppError()

    fun <T> toResult(): Result<T> = Result.failure(this)
}

