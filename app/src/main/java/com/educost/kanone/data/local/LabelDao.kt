package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.educost.kanone.data.model.entity.LabelCardCrossRef
import com.educost.kanone.data.model.entity.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Query(
        """
        SELECT * FROM labels
        WHERE id IN (
            SELECT label_id FROM labels_and_cards
            WHERE card_id IN (:cardIds)
        )
    """
    )
    fun observeLabels(cardIds: List<Long>): Flow<List<LabelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: LabelEntity): Long

    @Insert
    suspend fun insertLabelCrossRef(labelCrossRef: LabelCardCrossRef)

    @Transaction
    @Insert
    suspend fun insertLabelWithCard(label: LabelEntity, cardId: Long): Long {
        val labelId = insertLabel(label)
        insertLabelCrossRef(LabelCardCrossRef(labelId, cardId))
        return labelId
    }

    @Delete
    suspend fun deleteLabel(label: LabelEntity)

}