package com.educost.kanone.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import com.educost.kanone.R
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    initialColor: Int? = null
) {

    var newColor by rememberSaveable {
        mutableIntStateOf(initialColor ?: Color.White.toArgb())
    }

    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass

    when (windowWidthSizeClass) {

        WindowHeightSizeClass.COMPACT -> CompactColorPicker(
            modifier = modifier,
            onDismiss = onDismiss,
            newColor = newColor,
            onNewColorChange = { newColor = it },
            onConfirm = onConfirm,
        )

        else -> NormalColorPicker(
            modifier = modifier,
            onDismiss = onDismiss,
            newColor = newColor,
            onNewColorChange = { newColor = it },
            onConfirm = onConfirm,
        )

    }

}

@Composable
private fun CompactColorPicker(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    newColor: Int,
    onNewColorChange: (Int) -> Unit,
    onConfirm: (Int) -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        text = {
            val controller = rememberColorPickerController()
            Column {
                Row(Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(newColor))
                    )

                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp),
                        controller = controller,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            onNewColorChange(colorEnvelope.color.toArgb())
                        },
                        initialColor = Color(newColor),
                    )
                }

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(35.dp),
                    controller = controller,
                    initialColor = Color(newColor)
                )

            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onConfirm(newColor)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.pick_color_dialog_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.pick_color_dialog_cancel_button))
            }
        }
    )
}

@Composable
private fun NormalColorPicker(
    modifier: Modifier = Modifier, onDismiss: () -> Unit,
    newColor: Int,
    onNewColorChange: (Int) -> Unit,
    onConfirm: (Int) -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(newColor))
            )
        },
        text = {
            val controller = rememberColorPickerController()
            Column {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        onNewColorChange(colorEnvelope.color.toArgb())
                    },
                    initialColor = Color(newColor),
                )

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                    initialColor = Color(newColor)
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onConfirm(newColor)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.pick_color_dialog_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.pick_color_dialog_cancel_button))
            }
        }
    )
}