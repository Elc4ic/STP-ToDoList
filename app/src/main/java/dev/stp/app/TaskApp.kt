package dev.stp.app

import android.app.Application
import dev.stp.app.di.dataModule
import dev.stp.app.di.domainModule
import dev.stp.app.di.networkModule
import dev.stp.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TaskApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TaskApp)

            modules(
                networkModule,
                dataModule,
                domainModule,
                viewModelModule
            )
        }
    }
}