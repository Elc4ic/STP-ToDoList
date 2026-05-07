package dev.stp.routes

import apiRoutes.Api
import dev.stp.service.UserService
import dto.AuthRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Route.authRoutes() {

    post(Api.Auth.Register.path) {
        val userService = call.application.get<UserService>()
        val request = call.receive<AuthRequest>()
        val response = userService.register(request)
        call.respond(HttpStatusCode.Created, response)
    }

    post(Api.Auth.Login.path) {
        val userService = call.application.get<UserService>()
        val request = call.receive<AuthRequest>()
        val response = userService.authenticate(request)
        call.respond(status = HttpStatusCode.OK, response)
    }

    post(Api.Auth.Refresh.path) {
        val userService = call.application.get<UserService>()
        val refreshToken = call.receive<String>()
        val response = userService.refresh(refreshToken)

        if (response != null) call.respond(response)
        else call.respond(HttpStatusCode.Unauthorized)
    }
}