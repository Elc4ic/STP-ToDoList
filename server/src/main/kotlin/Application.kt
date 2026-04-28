package dev.stp

import dev.stp.infrastructure.db.DatabaseFactory
import dev.stp.plugins.configureDI
import dev.stp.plugins.configureRouting
import dev.stp.plugins.configureSecurity
import dev.stp.plugins.configureSerialization
import dev.stp.plugins.configureStatusPages
import io.ktor.server.application.Application

fun Application.module() {
    val jwtName = "auth-jwt"

    DatabaseFactory.init()
    configureSerialization()
    configureDI()
    configureStatusPages()
    configureSecurity(jwtName)
    configureRouting(jwtName)
}
