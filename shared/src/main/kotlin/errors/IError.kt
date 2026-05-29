package errors

sealed class IError(val message: String) {

    sealed class Auth(message: String) : IError(message) {
        class InvalidCredentials : Auth("Неверный логин или пароль")
        class InvalidToken : Auth("Сессия устарела. Войдите в аккаунт заново")
        class UserNotFound : Auth("Пользователь с такими данными не найден")
        data class UserAlreadyExists(val login: String) :
            Auth("Пользователь с логином $login уже зарегистрирован")

        class LoginFieldEmpty : Auth("Логин не должен быть пустым")
        class PasswordFieldEmpty : Auth("Пароль не должен быть пустым")
        class PasswordTooShort : Auth("Пароль слишком короткий (минимум 6 символов)")
        class PasswordTooLong : Auth("Пароль слишком длинный (максимум 50 символов)")
        class LoginTooLong : Auth("Пароль слишком длинный (максимум 50 символов)")
        class LoginHaveSpecSimbol : Auth("Логин содержит спецсимволы @#\\$%^&*()")
        class LoginTooShort : Auth("Логин слишком короткий (минимум 3 символов)")
        class CannotSaveToken : Auth("Не удалось сохранить ключ авторизации в хранилище")
    }

    sealed class DB(message: String) : IError(message) {
        class NotFoundRemote : DB("Запрошенные данные на сервере не найдены")
        class CannotSaveRemote : DB("Не удалось сохранить изменения на сервере")

        data class WriteErrorRemote(val throwable: Throwable) :
            DB("Ошибка записи на сервере: ${throwable.message}")

        class NotFoundLocal : DB("Запись в локальной базе данных не найдена")
        class CannotSaveLocal : DB("Не удалось записать данные в локальную БД")

        data class WriteErrorLocal(val throwable: Throwable) :
            DB("Ошибка локальной базы данных: ${throwable.message}")
    }

    class NetworkError : IError("Отсутствует интернет-соединение. Проверьте сеть")
    class ServerError : IError("Внутренняя ошибка сервера. Попробуйте позже")
    class Unknown(message: String) : IError(message)
}
