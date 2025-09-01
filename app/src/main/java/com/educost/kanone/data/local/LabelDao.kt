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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLabel(label: LabelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLabelCrossRef(labelCrossRef: LabelCardCrossRef)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createLabelAndAssociateWithCard(label: LabelEntity, cardId: Long): Long {
        val labelId = createLabel(label)
        createLabelCrossRef(LabelCardCrossRef(labelId, cardId))
        return labelId
    }

    @Delete
    suspend fun deleteLabel(label: LabelEntity)

    @Query(
        """
        SELECT *
        FROM labels
        WHERE board_id = (
            SELECT board_id
            FROM columns
            WHERE id = (
                SELECT column_id
                FROM cards
                WHERE id = :cardId
            )
        )
        """
    )
    fun observeLabels(cardId: Long): Flow<List<LabelEntity>>

    @Query(
        """
        SELECT board_id
            FROM columns
            WHERE id = (
                SELECT column_id
                FROM cards
                WHERE id = :cardId
            )
    """
    )
    suspend fun getCardBoardId(cardId: Long): Long

}