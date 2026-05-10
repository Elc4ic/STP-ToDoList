package apiRoutes

import io.github.cdimascio.dotenv.dotenv


sealed class Api(val path: String) {
    val dotenv = dotenv { ignoreIfMissing = true }
    val host = dotenv["HOST"] ?: "85.137.167.193"
    val port = dotenv["PORT"] ?: "8080"

    sealed class Auth(path: String) : Api("/api/auth/$path") {
        object Login : Auth("login")
        object Register : Auth("register")
        object Refresh : Auth("refresh")
    }

    sealed class Tasks(path: String) : Api("/api/tasks/$path") {
        object Sync : Tasks("sync")

        object GetAll : Tasks("get")
    }

    fun url() = "http://$host:$port$path"
}

