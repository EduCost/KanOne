package com.educost.kanone.presentation.screens.card.components

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
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.domain.model.Task
import com.educost.kanone.presentation.screens.board.BoardIntent
import com.educost.kanone.presentation.screens.card.CardIntent
import com.educost.kanone.presentation.screens.card.CardUiState
import com.educost.kanone.presentation.screens.card.utils.CardAppBarType

@Composable
fun CardTasks(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    state: CardUiState,
    onIntent: (CardIntent) -> Unit
) {

    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.appBarType) {
        if (state.appBarType == CardAppBarType.ADD_TASK) {
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

                Spacer(Modifier.height(8.dp))

                Column {
                    tasks.forEach { task ->
                        TaskItem(task = task, onCheckedChange = {/*TODO*/ })
                    }

                }

            }

            if (state.appBarType == CardAppBarType.ADD_TASK) {
                CreateTask(
                    description = state.createTaskState.description,
                    isCompleted = state.createTaskState.isCompleted,
                    onCheckedChange = { onIntent(CardIntent.OnCreateTaskIsCompletedChanged(it)) },
                    onDescriptionChanged = { onIntent(CardIntent.OnCreateTaskDescriptionChanged(it)) },
                    onConfirm = { onIntent(CardIntent.ConfirmTaskCreation) }
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

    val keyboardController = LocalSoftwareKeyboardController.current

    Row(modifier = modifier, ) {

        Checkbox(checked = isCompleted, onCheckedChange = { onCheckedChange(it) })

        BasicTextField(
            value = description,
            onValueChange = { onDescriptionChanged(it) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onConfirm()
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier) {

        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = task.description
        )

    }
}
