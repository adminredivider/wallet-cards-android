package com.digitalwallet.mobilecards.domain.repository

import com.digitalwallet.mobilecards.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    suspend fun addCard(card: Card)
    fun getAllCards(): Flow<List<Card>>
    suspend fun deleteCard(card: Card)
    suspend fun updateCard(card: Card)
    suspend fun getCardById(cardId: Int): Card?
}

