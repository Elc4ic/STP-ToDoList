package dev.stp.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.request.receiveText
import kotlinx.coroutines.runBlocking

fun Application.configureLogging(){
    install(DoubleReceive)
    install(CallLogging) {
        format { call ->
            val requestBody = runBlocking { call.receiveText() }
            "Request Body: $requestBody"
        }
    }
}