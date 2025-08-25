package com.educost.kanone.presentation.screens.card.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Task
import com.educost.kanone.presentation.screens.card.CardIntent
import com.educost.kanone.presentation.screens.card.CardUiState
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType
import com.educost.kanone.presentation.theme.KanOneTheme
import kotlin.math.roundToInt

@Composable
fun CardTasks(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    state: CardUiState,
    onIntent: (CardIntent) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.appBarType) {
        if (state.appBarType == CardAppBarType.ADD_TASK || state.appBarType == CardAppBarType.EDIT_TASK) {
            focusManager.moveFocus(FocusDirection.Down)
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Filled.Checklist,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = stringResource(R.string.card_tasks))

                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = { onIntent(CardIntent.StartCreatingTask) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.card_add_task_button_content_description)
                    )
                }

            }

            if (tasks.isNotEmpty()) {

                Column {
                    tasks.forEach { task ->
                        TaskItem(
                            modifier = Modifier.clickable {
                                onIntent(
                                    CardIntent.StartEditingTask(
                                        task.id
                                    )
                                )
                            },
                            task = task,
                            isEditing = state.appBarType == CardAppBarType.EDIT_TASK && state.editTaskState.taskId == task.id,
                            newDescription = state.editTaskState.description,
                            onCheckedChange = {
                                onIntent(
                                    CardIntent.OnTaskCheckedChange(
                                        taskId = task.id,
                                        isChecked = it
                                    )
                                )
                            },
                            onDescriptionChange = {
                                onIntent(
                                    CardIntent.OnTaskDescriptionChange(
                                        taskId = task.id,
                                        description = it
                                    )
                                )
                            },
                            onConfirm = {
                                keyboardController?.hide()
                                onIntent(CardIntent.ConfirmTaskEdit)
                            },
                            onDelete = {
                                onIntent(CardIntent.DeleteTask(task.id))
                            }
                        )
                    }
                }

            }

            if (state.appBarType == CardAppBarType.ADD_TASK) {
                CreateTask(
                    description = state.createTaskState.description,
                    isCompleted = state.createTaskState.isCompleted,
                    onCheckedChange = { onIntent(CardIntent.OnCreateTaskIsCompletedChanged(it)) },
                    onDescriptionChanged = { onIntent(CardIntent.OnCreateTaskDescriptionChanged(it)) },
                    onConfirm = {
                        keyboardController?.hide()
                        onIntent(CardIntent.ConfirmTaskCreation)
                    }
                )
            }

            // Padding
            if (state.appBarType == CardAppBarType.ADD_TASK || tasks.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
            }

        }
    }
}


@Composable
private fun CreateTask(
    modifier: Modifier = Modifier,
    description: String,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onConfirm: () -> Unit,
) {


    Row(modifier = modifier) {

        Checkbox(checked = isCompleted, onCheckedChange = { onCheckedChange(it) })

        TaskTextField(
            value = description,
            onValueChange = { onDescriptionChanged(it) },
            onConfirm = { onConfirm() }
        )

    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    isEditing: Boolean,
    newDescription: String,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDelete: () -> Unit
) {

    var deleteIconWidth by remember { mutableFloatStateOf(0f) }
    var deleteIconPosition by remember { mutableFloatStateOf(0f) }
    var deleteIconScale by remember { mutableFloatStateOf(0f) }
    val deleteIconScaleAnimated by animateFloatAsState(deleteIconScale)
    val isPastDeleteThreshold by remember {
        derivedStateOf {
            deleteIconScale > 0.9f
        }
    }

    val rowAlpha = animateFloatAsState(
        targetValue = if (isPastDeleteThreshold) 0.7f else 1f,
        animationSpec = tween(durationMillis = 500, easing = EaseOut)
    )
    var rowOffset by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition()
    val shakeAnimation = infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TaskItemShakeRotation"
    )

    Box {

        IconButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .onGloballyPositioned {
                        deleteIconWidth = it.size.width.toFloat()
                        deleteIconPosition = it.positionInParent().x
                    }
                    .graphicsLayer(
                        scaleX = deleteIconScaleAnimated,
                        scaleY = deleteIconScaleAnimated,
                        alpha = deleteIconScaleAnimated,
                        rotationZ = if (isPastDeleteThreshold) shakeAnimation.value else 0f,
                    )

            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .offset { IntOffset(x = rowOffset.roundToInt(), y = 0) }
                .graphicsLayer(
                    alpha = rowAlpha.value
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->

                            rowOffset = (rowOffset + dragAmount).coerceIn(
                                minimumValue = 0f,
                                maximumValue = (deleteIconPosition + deleteIconWidth) + 10
                            )

                            val iconEndPosition = deleteIconPosition + deleteIconWidth

                            deleteIconScale = if (rowOffset > deleteIconPosition) {
                                rowOffset / iconEndPosition
                            } else {
                                0f
                            }

                        },
                        onDragEnd = {
                            if (isPastDeleteThreshold) {
                                onDelete()
                            }
                            rowOffset = 0f
                            deleteIconScale = 0f
                        }
                    )
                }
        ) {

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )

            if (isEditing) {
                TaskTextField(
                    value = newDescription,
                    onValueChange = onDescriptionChange,
                    onConfirm = onConfirm
                )
            } else {
                Text(
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                    text = task.description
                )
            }

        }
    }
}

@Composable
private fun TaskTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        value = value,
        onValueChange = { onValueChange(it) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onConfirm() }
        )
    )
}


@PreviewLightDark
@Composable
private fun CardTasksPreview() {
    KanOneTheme {
        Surface {
            CardTasks(
                modifier = Modifier.padding(16.dp),
                tasks = listOf(
                    Task(
                        id = 0,
                        description = "Task 1",
                        isCompleted = false,
                        position = 0
                    ),
                    Task(
                        id = 1,
                        description = "Task 2",
                        isCompleted = true,
                        position = 1
                    )
                ),
                state = CardUiState(),
                onIntent = {}
            )
        }
    }
}