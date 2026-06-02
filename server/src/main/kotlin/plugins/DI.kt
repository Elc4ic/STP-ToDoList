package dev.stp.plugins

import dev.stp.domain.repository.PostgresTaskRepository
import dev.stp.domain.repository.PostgresUserRepository
import dev.stp.domain.repository.TaskRepository
import dev.stp.domain.repository.UserRepository
import dev.stp.infrastructure.security.TokenManager
import dev.stp.service.TaskService
import dev.stp.service.TaskServiceImpl
import dev.stp.service.UserService
import dev.stp.service.UserServiceImpl
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(module {
            single { TokenManager() }
            single { PostgresUserRepository() as UserRepository }
            single { PostgresTaskRepository() as TaskRepository }
            single<UserService> { UserServiceImpl(get(), get()) }
            single<TaskService> { TaskServiceImpl(get()) }
        })
    }
}