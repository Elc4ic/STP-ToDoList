package dev.stp.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondText(
                cause.message ?: "Invalid argument",
                status = HttpStatusCode.BadRequest
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText(
                cause.message ?: "Something went wrong",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}