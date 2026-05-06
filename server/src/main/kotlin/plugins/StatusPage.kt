package dev.stp.plugins

import errors.AppError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppError.Auth.UserAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, cause.message ?: "Логин занят")
        }

        exception<AppError.Auth.InvalidCredentials> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, cause.message ?: "")
        }

        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, "Внутренняя ошибка сервера")
        }
    }
}