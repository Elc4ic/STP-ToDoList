package dev.stp

import dev.stp.infrastructure.db.DatabaseFactory
import dev.stp.plugins.configureDI
import dev.stp.plugins.configureLogging
import dev.stp.plugins.configureRouting
import dev.stp.plugins.configureSecurity
import dev.stp.plugins.configureSerialization
import io.ktor.server.application.Application

fun Application.module() {
    val jwtName = "auth-jwt"

    val jdbcUrl = environment.config.propertyOrNull("database.url")?.getString()
    val driver = environment.config.propertyOrNull("database.driver")?.getString()
    val user = environment.config.propertyOrNull("database.user")?.getString()
    val pass = environment.config.propertyOrNull("database.password")?.getString()

    if (jdbcUrl != null && driver != null) {
        DatabaseFactory.init(
            jdbcUrl = jdbcUrl,
            driverClassName = driver,
            user = user ?: "postgres",
            pass = pass ?: "postgres"
        )
    } else {
        DatabaseFactory.init()
    }

    configureLogging()
    configureSerialization()
    configureDI()
    configureSecurity(jwtName)
    configureRouting(jwtName)
}
