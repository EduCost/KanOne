package com.educost.kanone.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.educost.kanone.domain.model.CardPriority
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
    val position: Int,
    val description: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "due_date") val dueDate: LocalDateTime? = null,
    val color: Int? = null,
    @ColumnInfo(name = "cover_file_name") val coverFileName: String? = null,
    val priority: CardPriority = CardPriority.NORMAL,
    @ColumnInfo(name = "column_id") val columnId: Long
)
