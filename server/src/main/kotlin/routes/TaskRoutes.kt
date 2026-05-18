package dev.stp.routes

import apiRoutes.Api
import arrow.core.raise.context.bind
import arrow.core.raise.either
import dev.stp.service.TaskService
import dto.SyncRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.get

fun Route.taskRoutes() {

    post(Api.Tasks.Sync.path) {
        val taskService = call.application.get<TaskService>()
        val request = call.receive<SyncRequest>()
        val result = either {
            val uuid = getUserUuid().bind()
            taskService.sync(uuid, request)
        }

        call.respondEither(result, HttpStatusCode.OK)
    }

    post(Api.Tasks.GetAll.path) {
        val taskService = call.application.get<TaskService>()

        val result = either {
            val uuid = getUserUuid().bind()
            taskService.getAll(uuid).bind()
        }

        call.respondEither(result, HttpStatusCode.OK)
    }
}

