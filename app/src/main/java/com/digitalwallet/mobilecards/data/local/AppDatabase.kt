package com.digitalwallet.mobilecards.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.digitalwallet.mobilecards.data.local.dao.CardDao
import com.digitalwallet.mobilecards.data.local.entity.CardEntity
import com.digitalwallet.mobilecards.domain.model.CardTypeConverter

@Database(
    entities = [CardEntity::class],
    version = 2
)
@TypeConverters(CardTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}
