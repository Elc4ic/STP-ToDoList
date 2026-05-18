package dev.stp.routes

import arrow.core.Either
import errors.AppError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <reified T : Any> ApplicationCall.respondEither(
    result: Either<AppError, T>,
    successStatus: HttpStatusCode = HttpStatusCode.OK
) {
    result.fold(
        ifLeft = { e ->
            when (e) {
                is AppError.Auth.Server.InvalidCredentials -> respond(
                    HttpStatusCode.Unauthorized,
                    "Неверный логин или пароль"
                )

                is AppError.Auth.Server.UserAlreadyExists -> respond(
                    HttpStatusCode.Conflict,
                    "Логин занят"
                )

                is AppError.Auth.Server.InvalidToken -> respond(
                    HttpStatusCode.Unauthorized,
                    "Неверный логин или пароль"
                )

                is AppError.Task.Server.DatabaseError -> respond(
                    HttpStatusCode.InternalServerError,
                    "Ошибка базы данных"
                )

                else -> respond(HttpStatusCode.InternalServerError, message = e.message ?: "ХЗ")
            }
        },
        ifRight = { data ->
            respond(successStatus, data)
        }
    )
}