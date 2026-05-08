package dev.stp.app.di

import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDataBase
import androidx.room.Room
import apiRoutes.Api
import dev.stp.app.data.datasource.TokenManager
import dev.stp.app.data.repository.AuthRepositoryImpl
import dev.stp.app.data.repository.NotificationRepositoryImpl
import dev.stp.app.data.repository.SyncRepositoryImpl
import dev.stp.app.data.repository.TaskRepositoryImpl
import dev.stp.app.domain.repository.AuthRepository
import dev.stp.app.domain.repository.NotificationRepository
import dev.stp.app.domain.repository.SyncRepository
import dev.stp.app.domain.repository.TaskRepository
import dev.stp.app.domain.usecases.AddTaskUseCase
import dev.stp.app.domain.usecases.DeleteTaskUseCase
import dev.stp.app.domain.usecases.EditTaskUseCase
import dev.stp.app.domain.usecases.GetAllTaskUseCase
import dev.stp.app.domain.usecases.GetTaskUseCase
import dev.stp.app.domain.usecases.SearchTaskUseCase
import dev.stp.app.domain.usecases.SwitchPinnedUseCase
import dev.stp.app.presentation.LogInScreen.LogInViewModel
import dev.stp.app.presentation.EditTaskScreen.EditTaskViewModel
import dev.stp.app.presentation.TasksScreen.TaskViewModel
import dto.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
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

    single<SyncRepository> { SyncRepositoryImpl(androidContext()) }

    single<NotificationRepository> { NotificationRepositoryImpl(androidContext()) }

    single<TaskRepository> {
        TaskRepositoryImpl(
            taskDao = get(),
            syncRepository = get(),
            notificationRepository = get()
        )
    }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}
val domainModule = module {

    factory {
        AddTaskUseCase(
            repository = get()
        )
    }
    factory {
        GetAllTaskUseCase(
            repository = get()
        )
    }

    factory {
        SearchTaskUseCase(
            repository = get()
        )
    }

    factory {
        SwitchPinnedUseCase(
            repository = get()
        )
    }
    factory {
        DeleteTaskUseCase(
            repository = get()
        )
    }

    factory {
        EditTaskUseCase(
            repository = get()
        )
    }

    factory {
        GetTaskUseCase(
            repository = get()
        )
    }


}
val viewModelModule = module {

    viewModel {
        TaskViewModel(
            getAllTaskUseCase = get(),
            switchPinnedUseCase = get(),
            searchTaskUseCase = get()
        )
    }

    viewModel {
        AddTaskViewModel(
            addTaskUseCase = get()
        )
    }

    viewModel {parameters->
        EditTaskViewModel(
            taskId = parameters.get(),
            editTaskUseCase = get(),
            deleteTaskUseCase = get(),
            getTaskUseCase = get()
        )
    }

    viewModel {
        LogInViewModel (
            logInUseCase = get()
        )
    }
}

val networkModule = module {
    single { TokenManager(androidContext()) }

    single<AuthRepository> {
        AuthRepositoryImpl(client = get(), tokenManager = get())
    }

    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) { level = LogLevel.BODY }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = get<TokenManager>().accessToken.first()
                        val refresh = get<TokenManager>().refreshToken.first()
                        if (token != null && refresh != null) {
                            BearerTokens(token, refresh)
                        } else null
                    }

                    refreshTokens {
                        val refreshToken = get<TokenManager>().refreshToken.first()
                            ?: return@refreshTokens null

                        try {
                            val response = client.post(Api.Auth.Refresh.url()) {
                                setBody(refreshToken)
                                markAsRefreshTokenRequest()
                            }.body<AuthResponse>()
                            get<TokenManager>().saveTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                            BearerTokens(
                                response.accessToken,
                                response.refreshToken
                            )
                        } catch (e: Exception) {
                            get<TokenManager>().clear()
                            null
                        }
                    }
                }
            }
        }
    }
}