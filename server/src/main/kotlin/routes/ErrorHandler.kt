package dev.stp.routes

import arrow.core.Either
import errors.IError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <reified T : Any> ApplicationCall.respondEither(
    result: Either<IError, T>,
    successStatus: HttpStatusCode = HttpStatusCode.OK
) {
    result.fold(
        ifLeft = { error ->
            val (status, message) = when (error) {
                is IError.Auth.LoginFieldEmpty,
                is IError.Auth.LoginTooShort,
                is IError.Auth.LoginTooLong,
                is IError.Auth.PasswordFieldEmpty,
                is IError.Auth.PasswordTooLong,
                is IError.Auth.PasswordTooShort,
                is IError.Auth.LoginHaveSpecSimbol
                    -> HttpStatusCode.BadRequest to error.message

                is IError.Auth.InvalidCredentials,
                is IError.Auth.InvalidToken
                    -> HttpStatusCode.Unauthorized to error.message

                is IError.Auth.UserNotFound,
                is IError.DB.NotFoundLocal,
                is IError.DB.NotFoundRemote
                    -> HttpStatusCode.NotFound to error.message

                is IError.Auth.UserAlreadyExists -> HttpStatusCode.Conflict to error.message

                is IError.NetworkError -> HttpStatusCode.BadGateway to error.message

                is IError.DB.CannotSaveRemote,
                is IError.DB.CannotSaveLocal,
                is IError.DB.WriteErrorLocal,
                is IError.DB.WriteErrorRemote,
                is IError.Auth.CannotSaveToken,
                is IError.ServerError,
                is IError.Unknown
                    -> HttpStatusCode.InternalServerError to error.message
            }
            respond(status, message)
        },
        ifRight = { data ->
            respond(successStatus, data)
        }
    )
}