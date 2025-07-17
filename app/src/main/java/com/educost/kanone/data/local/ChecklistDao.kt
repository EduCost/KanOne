package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educost.kanone.data.model.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {

    @Query("SELECT * FROM checklists WHERE card_id IN (:cardIds)")
    fun observeChecklists(cardIds: List<Long>): Flow<List<ChecklistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: ChecklistEntity)

    @Delete
    suspend fun deleteChecklist(checklist: ChecklistEntity)

}