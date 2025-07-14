package com.educost.kanone.data.local

import androidx.room.Delete
import androidx.room.Insert
import com.educost.kanban2.data.model.entity.ColumnEntity

interface ColumnDao {

    @Insert
    suspend fun insertColumn(column: ColumnEntity)

    @Delete
    suspend fun deleteColumn(column: ColumnEntity)
}