package apiRoutes

sealed class Api(val path: String) {
    sealed class Auth(path: String) : Api(path) {
        object Login : Auth("/auth/login")
        object Register : Auth("/auth/login")
        object Refresh : Auth("/auth/refresh")
    }

    sealed class Tasks(path: String) : Api(path) {
        object Home : Tasks("/api/tasks/home")
        object Sync : Tasks("/api/tasks/sync")

        object Get : Tasks("/api/tasks/get")
    }
}

