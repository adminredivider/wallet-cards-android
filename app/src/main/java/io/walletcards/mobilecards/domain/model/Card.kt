package io.walletcards.mobilecards.domain.model

import androidx.room.TypeConverter

data class Card(
    val id: Int = 0,
    val cardNumber: String,
    val cardExpiry: String,
    val cardHolderName: String,
    val cvv: String,
    val bankName: String,
    val cardType: CardType,
    val cardColor: String,
    var cardDesignBackgroundId: Int? = null,
    var notes: String? = null,
    var dummyCardId :Int? = null
)


enum class CardType {
    AMERICAN_EXPRESS,
    DINERS_CLUB,
    DISCOVER,
    JCB,
    MASTER_CARD,
    MAESTRO,
    RUPAY,
    VISA,
    ELO,
    UNIONPAY,
    OTHER
}

class CardTypeConverter {

    @TypeConverter
    fun fromCardType(cardType: CardType): String {
        return cardType.name
    }

    @TypeConverter
    fun toCardType(value: String): CardType {
        return CardType.valueOf(value)
    }
}
