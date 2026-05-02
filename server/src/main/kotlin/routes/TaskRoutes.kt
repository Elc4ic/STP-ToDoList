package dev.stp.routes

import apiRoutes.Api
import dev.stp.service.TaskService
import dto.SyncRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import java.util.UUID

fun RoutingContext.getUserId() = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.toString()

fun Route.taskRoutes() {
    val taskService by inject<TaskService>()

    post(Api.Tasks.Sync.path) {
        val userId = getUserId() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val uuid = UUID.fromString(userId)

        val request = call.receive<SyncRequest>()
        val response = taskService.sync(uuid, request)
        call.respond(response)
    }

    post(Api.Tasks.Get.path) {
        val userId = getUserId() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val uuid = UUID.fromString(userId)

        val response = taskService.getAll(uuid)
        call.respond(response)
    }
}

