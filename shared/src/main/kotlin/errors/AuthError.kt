package errors

sealed class AppError(msg: String) : Exception(msg) {
    sealed class Auth(msg: String) : AppError(msg) {
        sealed class Server(msg: String) : Auth(msg) {
            class InvalidCredentials : Auth("Неверный логин или пароль")
            class UserNotFound : Auth("Неверный логин или пароль")
            class UserAlreadyExists(login: String) :
                Auth("Пользователь с логином $login уже существует")
        }

        sealed class Client(msg: String) : Auth(msg) {
            class LoginFieldEmpty : Auth("поле логина пусто")
            class PasswordFieldEmpty : Auth("поле пароля пусто")
            class PasswordTooShort : Auth("Пароль слишком короткий")
        }
    }

    sealed class Task(msg: String) : AppError(msg) {
        sealed class Server(msg: String) : Task(msg) {
            class CannotSaveTask : Auth("Неудалось сохранить задачу удаленно")
        }

        sealed class Client(msg: String) : Task(msg) {
            class CannotSaveTask : Auth("Неудалось сохранить задачу локально")
        }
    }

    class NetworkError : AppError("Ошибка сети")
    class ServerError : AppError("Ошибка сервера")
    class Unknown(message: String) : AppError("Ошибка: $message")

    fun <T> toResult(): Result<T> = Result.failure(this)
}

