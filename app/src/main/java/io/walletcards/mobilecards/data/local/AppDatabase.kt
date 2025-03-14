package io.walletcards.mobilecards.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.walletcards.mobilecards.data.local.dao.CardDao
import io.walletcards.mobilecards.data.local.entity.CardEntity
import io.walletcards.mobilecards.domain.model.CardTypeConverter

@Database(
    entities = [CardEntity::class],
    version = 2
)
@TypeConverters(CardTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}
