package errors

sealed class AppError(val message: String) {

    sealed class Server(message: String) : AppError(message) {

        sealed class Auth(message: String) : Server(message) {
            class InvalidCredentials : Auth("Неверный логин или пароль")
            class InvalidToken : Auth("Сессия устарела. Войдите в аккаунт заново")
            class UserNotFound : Auth("Пользователь с такими данными не найден")

            data class UserAlreadyExists(val login: String) :
                Auth("Пользователь с логином $login уже зарегистрирован")
        }

        sealed class DB(message: String) : Server(message) {
            class NotFound : DB("Запрошенные данные на сервере не найдены")
            class CannotSave : DB("Не удалось сохранить изменения на сервере")

            data class WriteError(val throwable: Throwable) :
                DB("Ошибка записи на сервере: ${throwable.message}")
        }
    }

    sealed class Client(message: String) : AppError(message) {

        sealed class Auth(message: String) : Client(message) {
            class LoginFieldEmpty : Auth("Логин не должен быть пустым")
            class PasswordFieldEmpty : Auth("Пароль не должен быть пустым")
            class PasswordTooShort : Auth("Пароль слишком короткий (минимум 6 символов)")
            class CannotSaveToken : Auth("Не удалось сохранить ключ авторизации в хранилище")
        }

        sealed class DB(message: String) : Client(message) {
            class NotFound : DB("Запись в локальной базе данных не найдена")
            class CannotSave : DB("Не удалось записать данные в локальную БД")

            data class WriteError(val throwable: Throwable) :
                DB("Ошибка локальной базы данных: ${throwable.message}")
        }
    }

    class NetworkError : AppError("Отсутствует интернет-соединение. Проверьте сеть")
    class ServerError : AppError("Внутренняя ошибка сервера. Попробуйте позже")
    class Unknown(message: String) : AppError(message)
}
