package io.walletcards.android.data.local.roommigration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object RoomMigration {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new column with a default value
            database.execSQL("ALTER TABLE credit_cards ADD COLUMN card_design_id INTEGER NOT NULL DEFAULT 0")
        }
    }
}