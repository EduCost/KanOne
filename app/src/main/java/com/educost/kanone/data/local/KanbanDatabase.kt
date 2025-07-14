package com.educost.kanone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ChecklistEntity
import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.data.model.entity.LabelCardCrossRef
import com.educost.kanone.data.model.entity.LabelEntity

@Database(
    entities = [
        AttachmentEntity::class,
        BoardEntity::class,
        CardEntity::class,
        ChecklistEntity::class,
        ColumnEntity::class,
        LabelCardCrossRef::class,
        LabelEntity::class
    ],
    version = 1
)
abstract class KanbanDatabase : RoomDatabase() {
    abstract fun attachmentDao(): AttachmentDao
    abstract fun boardDao(): BoardDao
    abstract fun cardDao(): CardDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun columnDao(): ColumnDao
    abstract fun labelDao(): LabelDao
}