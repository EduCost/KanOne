package com.educost.kanone.presentation.screens.card.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Task
import com.educost.kanone.presentation.screens.card.CardIntent
import com.educost.kanone.presentation.screens.card.CardUiState
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType
import com.educost.kanone.presentation.theme.KanOneTheme

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
                Spacer(Modifier.height(12.dp))
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
    onConfirm: () -> Unit
) {
    Row(modifier = modifier) {

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