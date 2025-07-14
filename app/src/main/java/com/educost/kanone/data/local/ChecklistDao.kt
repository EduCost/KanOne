package com.educost.kanone.data.local

import androidx.room.Delete
import androidx.room.Insert
import com.educost.kanban2.data.model.entity.ChecklistEntity

interface ChecklistDao {

    @Insert
    suspend fun insertChecklist(checklist: ChecklistEntity)

    @Delete
    suspend fun deleteChecklist(checklist: ChecklistEntity)

}