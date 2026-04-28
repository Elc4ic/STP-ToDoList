package apiRoutes


object Api {
    object Auth {
        const val LOGIN = "/auth/login"
        const val REGISTER = "/auth/register"
    }

    object Tasks {
        const val HOME = "/api/tasks"
        const val DETAILS = "/api/tasks/{id}"

        fun getDetails(id: String) = "/api/tasks/$id"
    }
}

// можно через Sealed но вроде retrofit на аннотацию требует compile const
//sealed class Api(val path: String) {
//    sealed class Auth(path: String) : ApiEnd(path) {
//        object Login : Auth("/auth/login")
//        object Register : Auth("/auth/login")
//    }
//
//    sealed class Task(path: String) : ApiEnd(path) {
//        object Home : Task("/api/tasks")
//        class Details(id: String) : Task("/api/tasks/$id")
//    }
//}

