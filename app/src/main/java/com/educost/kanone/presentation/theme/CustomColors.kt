package com.educost.kanone.presentation.theme

import androidx.compose.ui.graphics.Color

data class ColorPalette(
    val base: Color,
    val onBase: Color,
)

object ColorPalettes {
    val Blue = ColorPalette(
        base = Color(0xFFB3E5FC),
        onBase = Color(0xFF000000),
    )

    val Pink = ColorPalette(
        base = Color(0xFFFFCDD2),
        onBase = Color(0xFF000000),
    )
}

enum class Palette {
    Blue, Pink
}

// TODO: Handle label palette
enum class LabelPalette {
    Blue, Pink
}


fun getPalette(
    palette: Palette,
): ColorPalette {
    return when(palette) {
        Palette.Blue -> ColorPalettes.Blue
        Palette.Pink -> ColorPalettes.Pink
    }
}
