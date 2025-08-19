package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.relation.CardWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun observeCard(cardId: Long): Flow<CardWithRelations>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createCard(card: CardEntity): Long

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Update
    suspend fun updateCards(cards: List<CardEntity>)


}