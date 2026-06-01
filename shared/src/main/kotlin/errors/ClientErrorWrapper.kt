package errors

import arrow.core.Either
import arrow.core.flatMap
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.UnknownHostException

suspend inline fun <reified T> HttpClient.safeRequest(
    crossinline call: suspend HttpClient.() -> HttpResponse,
    crossinline mapError: (HttpStatusCode) -> IError? = { null }
): Either<IError, T> {

    val responseResult = Either.catch {
        this.call()
    }.mapLeft { error ->
        when (error) {
            is ConnectTimeoutException,
            is UnknownHostException,
            is ConnectException -> IError.NetworkError()

            is SerializationException -> IError.Unknown("Ошибка парсинга данных")
            else -> IError.Unknown(error.message ?: "Неизвестная ошибка")
        }
    }

    return responseResult.flatMap { response ->
        val status = response.status

        if (status.isSuccess()) {
            Either.catch {
                if (T::class == Unit::class) Unit as T
                else response.body<T>()
            }.mapLeft { IError.Unknown("Ошибка десериализации: ${it.message}") }
        } else {
            val error = mapError(status) ?: when (status) {
                HttpStatusCode.Unauthorized -> IError.Auth.InvalidCredentials()
                HttpStatusCode.NotFound -> IError.Auth.UserNotFound()
                HttpStatusCode.InternalServerError -> IError.ServerError()
                else -> IError.Unknown("Код ошибки: ${status.value}")
            }
            Either.Left(error)
        }
    }
}