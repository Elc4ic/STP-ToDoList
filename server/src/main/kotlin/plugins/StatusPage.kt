package dev.stp.plugins

import errors.AppError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {

        exception<AppError.Auth.Server.UserAlreadyExists> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                cause.message ?: "Логин занят"
            )
        }

        exception<AppError.Auth.Server.InvalidCredentials> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                cause.message ?: "Неверный логин или пароль"
            )
        }

        exception<AppError.Auth.Server.UserNotFound> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                cause.message ?: "Неверный логин или пароль"
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                cause.message ?: "Внутренняя ошибка сервера"
            )
        }
    }
}