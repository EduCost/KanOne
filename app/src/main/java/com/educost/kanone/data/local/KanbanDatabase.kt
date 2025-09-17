package com.educost.kanone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.educost.kanone.data.model.entity.AttachmentEntity
import com.educost.kanone.data.model.entity.BoardEntity
import com.educost.kanone.data.model.entity.CardEntity
import com.educost.kanone.data.model.entity.ColumnEntity
import com.educost.kanone.data.model.entity.LabelCardCrossRef
import com.educost.kanone.data.model.entity.LabelEntity
import com.educost.kanone.data.model.entity.TaskEntity

@Database(
    entities = [
        AttachmentEntity::class,
        BoardEntity::class,
        CardEntity::class,
        TaskEntity::class,
        ColumnEntity::class,
        LabelCardCrossRef::class,
        LabelEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class KanbanDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "kanban_db"
    }

    abstract fun attachmentDao(): AttachmentDao
    abstract fun boardDao(): BoardDao
    abstract fun cardDao(): CardDao
    abstract fun taskDao(): TaskDao
    abstract fun columnDao(): ColumnDao
    abstract fun labelDao(): LabelDao

}