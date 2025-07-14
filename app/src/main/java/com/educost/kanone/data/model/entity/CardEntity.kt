package com.educost.kanone.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.educost.kanone.presentation.theme.Palette
import java.time.LocalDateTime

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = ColumnEntity::class,
            parentColumns = ["id"],
            childColumns = ["column_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "due_date") val dueDate: LocalDateTime? = null,
    @ColumnInfo(name = "thumbnail_file_name") val thumbnailFileName: String? = null,
    val position: Int,
    val color: Palette,
    @ColumnInfo(name = "column_id") val columnId: Long
)
