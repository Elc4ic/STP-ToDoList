package dev.stp.routes

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import errors.AppError
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingContext
import java.util.UUID

fun RoutingContext.getUserUuid(): Either<AppError, UUID> = either {
    val userIdStr = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
    ensureNotNull(userIdStr) { AppError.Auth.Server.InvalidToken() }

    Either.catch { UUID.fromString(userIdStr) }
        .mapLeft { AppError.Auth.Server.InvalidToken() }
        .bind()
}