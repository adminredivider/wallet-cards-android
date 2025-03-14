package io.walletcards.android.data.repository

import io.walletcards.android.data.local.dao.CardDao
import io.walletcards.android.data.local.mapper.toDomain
import io.walletcards.android.data.local.mapper.toEntity
import io.walletcards.android.domain.model.Card
import io.walletcards.android.domain.repository.CardRepository
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
