package com.digitalwallet.mobilecards.data.local.mapper

import com.digitalwallet.mobilecards.data.local.entity.CardEntity
import com.digitalwallet.mobilecards.domain.model.Card

fun Card.toEntity(): CardEntity {
    return CardEntity(
        id = this.id,
        cardNumber = this.cardNumber,
        cardExpiry = this.cardExpiry,
        cardHolderName = this.cardHolderName,
        cvv = this.cvv,
        bankName = this.bankName,
        cardType = this.cardType,
        cardColor = this.cardColor,
        cardDesignId = this.cardDesignBackgroundId ?: 0,
        notes = this.notes ?: ""
    )
}

fun CardEntity.toDomain(): Card {
    return Card(
        id = this.id,
        cardNumber = this.cardNumber,
        cardExpiry = this.cardExpiry,
        cardHolderName = this.cardHolderName,
        cvv = this.cvv,
        bankName = this.bankName,
        cardType = this.cardType,
        cardColor = this.cardColor,
        cardDesignBackgroundId = this.cardDesignId,
        notes = this.notes
    )
}