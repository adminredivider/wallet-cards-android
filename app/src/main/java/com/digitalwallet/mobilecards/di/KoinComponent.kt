package com.digitalwallet.mobilecards.di

import com.digitalwallet.mobilecards.domain.repository.CardRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module(
    includes = [
        DatabaseModule::class,
    ]
)
@ComponentScan("com.digitalwallet.mobilecards")
class KoinComponent
@Module
class ConfigModule {
    @Single
    fun provideRepository(
        realRepo: CardRepository,
    ): CardRepository = realRepo
}