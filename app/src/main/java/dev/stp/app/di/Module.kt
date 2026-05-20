package dev.stp.app.di

import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDataBase
import androidx.room.Room
import apiRoutes.Api
import dev.stp.app.data.datasource.DataStoreTokenStore
import dev.stp.app.data.datasource.DeadlineNotificationWorker
import dev.stp.app.data.datasource.SyncWorker
import dev.stp.app.data.datasource.TokenStore
import dev.stp.app.data.repository.AuthRepositoryImpl
import dev.stp.app.data.repository.NotificationRepositoryImpl
import dev.stp.app.data.repository.SyncRepositoryImpl
import dev.stp.app.data.repository.TaskRepositoryImpl
import dev.stp.app.domain.repository.AuthRepository
import dev.stp.app.domain.repository.NotificationRepository
import dev.stp.app.domain.repository.SyncRepository
import dev.stp.app.domain.repository.TaskRepository
import dev.stp.app.presentation.LogInScreen.LogInViewModel
import dev.stp.app.presentation.EditTaskScreen.EditTaskViewModel
import dev.stp.app.presentation.RegistrationScreen.RegistrationViewModel
import dev.stp.app.presentation.TasksScreen.TaskViewModel
import dto.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import ru.dedmos.todo.presentation.AddTaskScreen.AddTaskViewModel

val dataModule = module {

    single<TaskDataBase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = TaskDataBase::class.java,
            name = "tasks.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single<TaskDao> {
        get<TaskDataBase>().taskDao()
    }

    single<SyncRepository> {
        SyncRepositoryImpl(
            context = androidContext(),
            taskDao = get(),
            client = get(),
            tokenStore = get()
        )
    }

    single<NotificationRepository> { NotificationRepositoryImpl(androidContext()) }

    single<TaskRepository> {
        TaskRepositoryImpl(
            taskDao = get(),
            syncRepository = get(),
            notificationRepository = get(),
            tokenStore = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            get(), get(), get()
        )
    }
}

val workerModule = module {
    worker { DeadlineNotificationWorker(get(), get()) }
    worker { SyncWorker(get(), get(), get(), get(), get()) }
}
val viewModelModule = module {
    viewModel {
        TaskViewModel(
            taskRepository = get(),
        )
    }

    viewModel {
        AddTaskViewModel(
            taskRepository = get()
        )
    }

    viewModel { parameters ->
        EditTaskViewModel(
            taskId = parameters.get(),
            taskRepository = get()
        )
    }

    viewModel {
        LogInViewModel(
            authRepository = get(),
            syncRepository = get()
        )
    }
    viewModel {
        RegistrationViewModel(
            authRepository = get(),
        )
    }
}

val networkModule = module {
    single<TokenStore> { DataStoreTokenStore(androidContext()) }

    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) { level = LogLevel.BODY }

            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = get<TokenStore>().accessToken.first().getOrNull()
                        val refresh = get<TokenStore>().refreshToken.first().getOrNull()
                        if (token != null && refresh != null) {
                            BearerTokens(token, refresh)
                        } else null
                    }

                    refreshTokens {
                        val refreshToken = get<TokenStore>().refreshToken.first().getOrNull()
                            ?: return@refreshTokens null

                        try {
                            val response = client.post(Api.Auth.Refresh.url()) {
                                setBody(refreshToken)
                                markAsRefreshTokenRequest()
                            }.body<AuthResponse>()
                            get<TokenStore>().saveTokens(
                                response.user,
                                response.accessToken,
                                response.refreshToken
                            )
                            BearerTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                        } catch (e: Exception) {
                            get<TokenStore>().clear()
                            null
                        }
                    }
                }
            }
        }
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            client = get(),
            tokenStore = get(),
            taskDao = get()
        )
    }
}