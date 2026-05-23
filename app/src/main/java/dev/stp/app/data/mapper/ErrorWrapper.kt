package dev.stp.app.data.mapper

import arrow.core.Either
import arrow.core.flatMap
import errors.AppError
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import java.net.UnknownHostException


suspend inline fun <reified T> safeApiCall(
    crossinline call: suspend () -> HttpResponse,
    noinline mapError: ((HttpStatusCode) -> AppError?) = { null }
): Either<AppError, T> {
    val responseResult = Either.catch {
        call()
    }.mapLeft { error ->
        when (error) {
            is ConnectTimeoutException, is UnknownHostException -> AppError.NetworkError()
            is SerializationException -> AppError.Unknown("Ошибка парсинга данных")
            else -> AppError.Unknown(error.message ?: "Неизвестная ошибка")
        }
    }
    return responseResult.flatMap { response ->
        val status = response.status

        if (status.isSuccess()) {
            Either.catch {
                if (T::class == Unit::class) Unit as T
                else response.body<T>()
            }.mapLeft { AppError.Unknown("Ошибка десериализации: ${it.message}") }
        } else {
            val error = mapError(status)
                ?: when (status) {
                    HttpStatusCode.Unauthorized -> AppError.Server.Auth.InvalidCredentials()
                    HttpStatusCode.NotFound -> AppError.Server.Auth.UserNotFound()
                    HttpStatusCode.InternalServerError -> AppError.ServerError()
                    else -> AppError.Unknown("Код ошибки: ${status.value}")
                }
            Either.Left(error)
        }
    }
}
