package dev.stp.app.data.mapper

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
): Result<T> {
    return try {
        val response = call()
        val status = response.status

        if (status.isSuccess()) {
            if (T::class == Unit::class) Result.success(Unit as T)
            else Result.success(response.body<T>())
        } else {
            val error = mapError(status)
                ?: when (status) {
                    HttpStatusCode.Unauthorized -> AppError.Auth.Server.InvalidCredentials()
                    HttpStatusCode.NotFound -> AppError.Auth.Server.UserNotFound()
                    HttpStatusCode.InternalServerError -> AppError.ServerError()
                    else -> AppError.Unknown("Код ошибки: ${status.value}")
                }
            Result.failure(error)
        }
    } catch (e: Exception) {
        val appError = when (e) {
            is ConnectTimeoutException, is UnknownHostException -> AppError.NetworkError()
            is SerializationException -> AppError.Unknown("Ошибка парсинга данных")
            is AppError -> e
            else -> AppError.Unknown(e.message ?: "Неизвестная ошибка")
        }
        Result.failure(appError)
    }
}

