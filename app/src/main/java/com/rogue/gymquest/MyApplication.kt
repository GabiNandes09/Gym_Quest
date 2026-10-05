package com.rogue.gymquest

import android.app.Application
import com.rogue.gymquest.di.appModule
import com.rogue.gymquest.di.databaseModule
import com.rogue.gymquest.di.preferencesModule
import com.rogue.gymquest.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule,
                preferencesModule,
                databaseModule,
                viewModelModule
            )
        }
    }
}
