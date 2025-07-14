package com.educost.kanone.data.local

import androidx.room.Delete
import androidx.room.Insert
import com.educost.kanone.data.model.entity.CardEntity

interface CardDao {

    @Insert
    suspend fun insertCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

}