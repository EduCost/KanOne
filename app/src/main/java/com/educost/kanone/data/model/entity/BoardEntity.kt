package com.educost.kanone.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "name") val name: String,

    // Settings
    @ColumnInfo(name = "zoom_percentage") val zoomPercentage: Float = 100f,
    @ColumnInfo(name = "show_images")     val showImages: Boolean = true,
    @ColumnInfo(name = "vertical_layout") val isOnVerticalLayout: Boolean = false
)

