package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.educost.kanone.data.model.entity.ColumnEntity

@Dao
interface ColumnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColumn(column: ColumnEntity): Long

    @Delete
    suspend fun deleteColumn(column: ColumnEntity)
}