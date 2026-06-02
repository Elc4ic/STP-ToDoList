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
        call.respondEither(userService.register(request), HttpStatusCode.Created)
    }

    post(Api.Auth.Login.path) {
        val userService = call.application.get<UserService>()
        val request = call.receive<AuthRequest>()
        call.respondEither(userService.authenticate(request), HttpStatusCode.OK)
    }

    post(Api.Auth.Refresh.path) {
        val userService = call.application.get<UserService>()
        val refreshToken = call.receive<String>()
        call.respondEither(userService.refresh(refreshToken), HttpStatusCode.OK)
    }
}