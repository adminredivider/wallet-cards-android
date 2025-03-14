package io.walletcards.mobilecards

import android.app.Application
import io.walletcards.mobilecards.di.KoinComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.ksp.generated.module
import timber.log.Timber


class WalletApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTimber()
    }

    private fun initKoin() {
        GlobalContext.startKoin {
            androidContext(this@WalletApp)
            androidLogger(level = Level.ERROR)
            modules(KoinComponent().module)
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}