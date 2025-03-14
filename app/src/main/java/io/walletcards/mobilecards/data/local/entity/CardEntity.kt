package io.walletcards.mobilecards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import io.walletcards.mobilecards.domain.model.CardType

@Entity(tableName = "credit_cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "card_number") val cardNumber: String,
    @ColumnInfo(name = "card_expiry") val cardExpiry: String,
    @ColumnInfo(name = "card_holder_name") val cardHolderName: String,
    @ColumnInfo(name = "cvv") val cvv: String,
    @ColumnInfo(name = "bank_name") val bankName: String,
    @ColumnInfo(name = "card_type") val cardType: CardType,
    @ColumnInfo(name = "card_color") val cardColor: String,
    @ColumnInfo(name = "card_design_id") val cardDesignId: Int ,
    @ColumnInfo(name = "notes") val notes: String
)
