package apiRoutes

const val host = "94.183.185.63"
const val port = "8080"
sealed class Api(val path: String) {
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

