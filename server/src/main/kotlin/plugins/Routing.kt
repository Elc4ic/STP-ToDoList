package dev.stp.plugins

import dev.stp.routes.authRoutes
import dev.stp.routes.taskRoutes
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing

fun Application.configureRouting(jwtName: String) {
    routing {
        authRoutes()
        authenticate(jwtName) {
            taskRoutes()
        }
    }
}