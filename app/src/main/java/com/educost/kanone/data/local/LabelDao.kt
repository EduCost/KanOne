package com.educost.kanone.data.local

import androidx.room.Delete
import androidx.room.Insert
import com.educost.kanban2.data.model.entity.LabelEntity

interface LabelDao {

    @Insert
    suspend fun insertLabel(label: LabelEntity)

    @Delete
    suspend fun deleteLabel(label: LabelEntity)

}