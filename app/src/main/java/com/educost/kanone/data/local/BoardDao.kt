package com.educost.kanone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educost.kanone.data.model.entity.BoardEntity

@Dao
interface BoardDao {

    @Query("SELECT * FROM boards WHERE id = :boardId")
    suspend fun getBoard(boardId: Long): BoardEntity

    @Query("SELECT * FROM boards")
    suspend fun getBoards(): List<BoardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity): Long

    @Delete
    suspend fun deleteBoard(board: BoardEntity)
}