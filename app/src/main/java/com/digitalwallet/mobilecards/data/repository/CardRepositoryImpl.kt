package com.digitalwallet.mobilecards.data.repository

import com.digitalwallet.mobilecards.data.local.dao.CardDao
import com.digitalwallet.mobilecards.data.local.mapper.toDomain
import com.digitalwallet.mobilecards.data.local.mapper.toEntity
import com.digitalwallet.mobilecards.domain.model.Card
import com.digitalwallet.mobilecards.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class CardRepositoryImpl(
    private val cardDao: CardDao,
) : CardRepository {

    override suspend fun addCard(card: Card) {
        cardDao.insertCard(card.toEntity())
    }

    override fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards().map {
            it.map { it1 ->
                it1.toDomain()
            }
        }
    }

    override suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(card.toEntity())
    }

    override suspend fun updateCard(card: Card) {
        cardDao.updateCard(card.toEntity())
    }

    override suspend fun getCardById(cardId: Int): Card? {
        return cardDao.getCardById(cardId)?.toDomain()
    }

}
