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
                is AppError.Server.Auth.InvalidCredentials -> respond(
                    HttpStatusCode.Unauthorized,
                    e.message
                )

                is AppError.Server.Auth.UserAlreadyExists -> respond(
                    HttpStatusCode.Conflict,
                    e.message
                )

                is AppError.Server.Auth.InvalidToken -> respond(
                    HttpStatusCode.Unauthorized,
                    e.message
                )

                is AppError.Server.DB.CannotSave -> respond(
                    HttpStatusCode.InternalServerError,
                    e.message
                )

                else -> respond(HttpStatusCode.InternalServerError, message = e.message)
            }
        },
        ifRight = { data ->
            respond(successStatus, data)
        }
    )
}