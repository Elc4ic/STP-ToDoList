package dev.stp

import dev.stp.routes.configureRouting
import io.ktor.server.application.Application

fun Application.rootModule() {
    configureRouting()
}
