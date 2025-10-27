package com.educost.kanone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
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
    version = 3
)
@TypeConverters(Converters::class)
abstract class KanbanDatabase : RoomDatabase() {

    abstract fun attachmentDao(): AttachmentDao
    abstract fun boardDao(): BoardDao
    abstract fun cardDao(): CardDao
    abstract fun taskDao(): TaskDao
    abstract fun columnDao(): ColumnDao
    abstract fun labelDao(): LabelDao

    companion object {
        const val DATABASE_NAME = "kanban_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE boards ADD COLUMN zoom_percentage REAL NOT NULL DEFAULT 100.0")
                connection.execSQL("ALTER TABLE boards ADD COLUMN show_images INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE boards ADD COLUMN vertical_layout INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("ALTER TABLE columns ADD COLUMN is_expanded INTEGER NOT NULL DEFAULT 1")
            }
        }
    }

}