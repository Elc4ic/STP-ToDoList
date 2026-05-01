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
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.taskRoutes() {
    val taskService by inject<TaskService>()

    post(Api.Tasks.Sync.path) {
        val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.toString()
            ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val uuid = UUID.fromString(userId)

        val request = call.receive<SyncRequest>()
        val response = newSuspendedTransaction(Dispatchers.IO) {
            taskService.sync(uuid, request)
        }

        call.respond(response)
    }

}