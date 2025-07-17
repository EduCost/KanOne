package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educost.kanone.data.model.entity.ColumnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColumnDao {

    @Query("SELECT * FROM columns WHERE board_id IN (:boardIds)")
    fun observeColumns(boardIds: List<Long>): Flow<List<ColumnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColumn(column: ColumnEntity): Long

    @Delete
    suspend fun deleteColumn(column: ColumnEntity)
}