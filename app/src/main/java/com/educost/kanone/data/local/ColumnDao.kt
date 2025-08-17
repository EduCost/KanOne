package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ChecklistEntity
import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.data.model.entity.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColumnDao {

    @Query("SELECT * FROM columns WHERE board_id = :boardId")
    fun observeColumns(boardId: Long): Flow<List<ColumnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createColumn(column: ColumnEntity): Long

    @Delete
    suspend fun deleteColumn(column: ColumnEntity)

    @Update
    suspend fun updateColumn(column: ColumnEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreCards(cards: List<CardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreLabels(labels: List<LabelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreChecklists(checklists: List<ChecklistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreAttachments(attachments: List<AttachmentEntity>)

    @Transaction
    suspend fun restoreWholeColumn(
        column: ColumnEntity,
        cards: List<CardEntity>,
        labels: List<LabelEntity>,
        checklists: List<ChecklistEntity>,
        attachments: List<AttachmentEntity>
    ) {
        createColumn(column)
        restoreCards(cards)
        restoreLabels(labels)
        restoreChecklists(checklists)
        restoreAttachments(attachments)
    }
}