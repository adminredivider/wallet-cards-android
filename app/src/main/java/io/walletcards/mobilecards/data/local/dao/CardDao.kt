package io.walletcards.mobilecards.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.walletcards.mobilecards.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Query("SELECT * FROM credit_cards")
    fun getAllCards(): Flow<List<CardEntity>>

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Update
    suspend fun updateCard(card: CardEntity)

    @Query("SELECT * FROM credit_cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): CardEntity?
}
