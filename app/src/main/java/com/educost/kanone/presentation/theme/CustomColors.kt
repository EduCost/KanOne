package com.educost.kanone.presentation.theme

import androidx.compose.ui.graphics.Color

data class ColorPalette(
    val base: Color,
    val onBase: Color,
)

object ColorPalettes {

    val default = ColorPalette(
        base = Color(0x00000000),
        onBase = Color(0xFFFFFFFF),
    )

    val blue = ColorPalette(
        base = Color(0xFFB3E5FC),
        onBase = Color(0xFF000000),
    )

    val pink = ColorPalette(
        base = Color(0xFFFFCDD2),
        onBase = Color(0xFF000000),
    )
}

enum class Palette {
    NONE, BLUE, PINK
}

// TODO: Handle label palette
enum class LabelPalette {
    NONE, BLUE, PINK
}


fun getPalette(
    palette: Palette,
): ColorPalette {
    return when(palette) {
        Palette.NONE -> ColorPalettes.default
        Palette.BLUE -> ColorPalettes.blue
        Palette.PINK -> ColorPalettes.pink
    }
}
