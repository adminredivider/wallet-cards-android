package io.walletcards.android.di

import android.content.Context
import androidx.room.Room
import io.walletcards.android.data.local.AppDatabase
import io.walletcards.android.data.local.dao.CardDao
import io.walletcards.android.data.local.roommigration.RoomMigration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabaseModule {
    @Single
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(RoomMigration.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Single
    fun provideCardDao(database: AppDatabase): CardDao {
        return database.cardDao()
    }
}
