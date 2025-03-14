package io.walletcards.android.domain.model

data class CCard(
    val cardNumber: String,
    val cardExpiry: String,
    val cardHolderName: String,
    val cvv: String,
    val bankName: String,
    val cardType: CardType,
    val cardColor: String
) {
    fun copyWith(
        cardNumber: String? = null,
        cardExpiry: String? = null,
        cardHolderName: String? = null,
        cvv: String? = null,
        bankName: String? = null,
        cardType: CardType? = null,
        cardColor: String? = null
    ): CCard {
        return CCard(
            cardNumber = cardNumber ?: this.cardNumber,
            cardExpiry = cardExpiry ?: this.cardExpiry,
            cardHolderName = cardHolderName ?: this.cardHolderName,
            cvv = cvv ?: this.cvv,
            bankName = bankName ?: this.bankName,
            cardType = cardType ?: this.cardType,
            cardColor = cardColor ?: this.cardColor
        )
    }
}
