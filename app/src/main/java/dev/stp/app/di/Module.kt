package dev.stp.app.di

import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDataBase
import androidx.room.Room
import dev.stp.app.data.repository.TaskRepositoryImpl
import dev.stp.app.domain.repository.TaskRepository
import dev.stp.app.domain.usecases.AddTaskUseCase
import dev.stp.app.domain.usecases.DeleteTaskUseCase
import dev.stp.app.domain.usecases.EditTaskUseCase
import dev.stp.app.domain.usecases.GetAllTaskUseCase
import dev.stp.app.domain.usecases.GetTaskUseCase
import dev.stp.app.domain.usecases.SearchTaskUseCase
import dev.stp.app.domain.usecases.SwitchPinnedUseCase
import dev.stp.app.presentation.TasksScreen.TaskViewModel
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

    single<TaskRepository> {
        TaskRepositoryImpl(
            taskDao = get()
        )
    }

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
val viewModelModule = module{

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





}