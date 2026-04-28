package dev.stp.routes

import apiRoutes.Api
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.taskRoutes() {

    get(Api.Tasks.HOME) {
        //TODO
    }

}