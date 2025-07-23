package com.educost.kanone.domain.model

import com.educost.kanone.presentation.theme.LabelPalette

data class Label(
    val id: Long,
    val name: String,
    val color: LabelPalette
)
