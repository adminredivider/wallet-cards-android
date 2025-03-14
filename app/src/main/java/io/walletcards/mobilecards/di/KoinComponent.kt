package io.walletcards.mobilecards.di

import io.walletcards.mobilecards.domain.repository.CardRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module(
    includes = [
        DatabaseModule::class,
    ]
)
@ComponentScan("io.walletcards.android")
class KoinComponent
@Module
class ConfigModule {
    @Single
    fun provideRepository(
        realRepo: CardRepository,
    ): CardRepository = realRepo
}