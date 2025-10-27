package com.educost.kanone.presentation.screens.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.domain.model.Label
import com.educost.kanone.domain.model.Task
import com.educost.kanone.presentation.components.ColorPickerDialog
import com.educost.kanone.presentation.components.DeleteBoardDialog
import com.educost.kanone.presentation.components.DialogRename
import com.educost.kanone.presentation.screens.board.components.BoardAppBar
import com.educost.kanone.presentation.screens.board.components.BoardColumn
import com.educost.kanone.presentation.screens.board.components.BoardModalBottomSheet
import com.educost.kanone.presentation.screens.board.components.ColumnCard
import com.educost.kanone.presentation.screens.board.components.HorizontalBoardLayout
import com.educost.kanone.presentation.screens.board.components.VerticalBoardLayout
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardUiState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
import com.educost.kanone.presentation.util.isWindowWidthCompact
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = hiltViewModel(),
    onNavigateToCard: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    boardId: Long
) {

    LaunchedEffect(Unit) {
        viewModel.onIntent(BoardIntent.ObserveBoard(boardId))
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(flow = viewModel.sideEffectFlow) { event ->
        when (event) {

            is BoardSideEffect.NavigateToCardScreen -> onNavigateToCard(event.cardId)

            is BoardSideEffect.OnNavigateBack -> onNavigateBack()

            is BoardSideEffect.NavigateToSettings -> onNavigateToSettings()

            is BoardSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()

                    val result = snackBarHostState.showSnackbar(
                        message = event.snackbarEvent.message.asString(context),
                        actionLabel = event.snackbarEvent.action?.label?.asString(context),
                        withDismissAction = event.snackbarEvent.withDismissAction,
                        duration = event.snackbarEvent.duration
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        event.snackbarEvent.action?.action?.invoke()
                    }
                }
            }
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        state.board?.let { board ->
            BoardScreen(
                modifier = modifier,
                board = board,
                state = state,
                onIntent = viewModel::onIntent,
                snackBarHostState = snackBarHostState
            )
        }
    }
}

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    board: BoardUi,
    state: BoardUiState,
    onIntent: (BoardIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val scrollChange = -panChange.x
        onIntent(BoardIntent.OnZoomChange(zoomChange, scrollChange))
    }
    val isWindowWidthCompact = isWindowWidthCompact()

    val isOnVerticalLayout by remember(board.isOnListView) {
        derivedStateOf {
            board.isOnListView && isWindowWidthCompact
        }
    }

    BackHandler(enabled = state.hasEditStates || state.isOnFullScreen) {
        onIntent(BoardIntent.OnBackPressed)
    }


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .transformable(transformableState)
            .pointerInput(isOnVerticalLayout) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, _ ->
                        onIntent(BoardIntent.OnDrag(change.position, isOnVerticalLayout))
                    },
                    onDragStart = { offset ->
                        onIntent(BoardIntent.OnDragStart(offset, isOnVerticalLayout))
                    },
                    onDragEnd = {
                        onIntent(BoardIntent.OnDragStop)
                    },
                    onDragCancel = {
                        onIntent(BoardIntent.OnDragStop)
                    }
                )
            },
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            BoardAppBar(
                boardName = board.name,
                type = state.topBarType,
                isDropdownMenuExpanded = state.isBoardDropdownMenuExpanded,
                isFullScreen = state.isOnFullScreen,
                onIntent = onIntent
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        when {
            isOnVerticalLayout -> {
                VerticalBoardLayout(
                    modifier = Modifier.padding(innerPadding),
                    board = board,
                    state = state,
                    onIntent = onIntent
                )
            }

            else -> HorizontalBoardLayout(
                modifier = Modifier.padding(innerPadding),
                board = board,
                state = state,
                onIntent = onIntent
            )
        }


        if (state.isModalSheetExpanded) {
            BoardModalBottomSheet(
                board = board,
                isFullScreen = state.isOnFullScreen,
                onIntent = onIntent
            )
        }

        if (state.columnEditState.isShowingColorPicker) {

            val initialColor = remember {
                state.board?.columns?.find {
                    it.id == state.columnEditState.editingColumnId
                }?.color
            }

            ColorPickerDialog(
                initialColor = initialColor,
                onDismiss = { onIntent(BoardIntent.CancelColumnColorEdit) },
                onConfirm = { onIntent(BoardIntent.ConfirmColumnColorEdit(it)) }
            )
        }

        if (state.isRenamingBoard) {
            DialogRename(
                onDismiss = { onIntent(BoardIntent.CancelBoardRename) },
                onConfirm = { onIntent(BoardIntent.ConfirmBoardRename(it)) },
                title = stringResource(R.string.dialog_rename_board_title),
            )
        }

        if (state.isShowingDeleteBoardDialog) {
            DeleteBoardDialog(
                onDismiss = { onIntent(BoardIntent.CancelBoardDeletion) },
                onDelete = { onIntent(BoardIntent.ConfirmBoardDeletion) }
            )
        }

        val localDensity = LocalDensity.current
        state.dragState.cardBeingDragged?.let { card ->
            ColumnCard(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = state.dragState.itemBeingDraggedOffset.y
                        translationX = state.dragState.itemBeingDraggedOffset.x
                        rotationZ = 2f
                    }
                    .width(with(localDensity) { card.coordinates.width.toDp() })
                    .height(with(localDensity) { card.coordinates.height.toDp() }),
                card = card,
                showImage = board.showImages,
                sizes =
                    if (isOnVerticalLayout) BoardSizes()
                    else board.sizes,
            )
        }
        state.dragState.columnBeingDragged?.let { column ->
            BoardColumn(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = state.dragState.itemBeingDraggedOffset.y
                        translationX = state.dragState.itemBeingDraggedOffset.x
                        rotationZ = 2f
                    }
                    .then(
                        other = when {
                            isOnVerticalLayout -> Modifier.fillMaxWidth()
                            else -> Modifier
                                .width(with(localDensity) { column.coordinates.width.toDp() })
                                .height(with(localDensity) { column.coordinates.height.toDp() })
                        }
                    ),
                column = column,
                state = BoardUiState(),
                onIntent = { },
                sizes =
                    if (isOnVerticalLayout) BoardSizes()
                    else board.sizes,
                isOnVerticalLayout = isOnVerticalLayout,
                showCardImages = board.showImages
            )
        }
    }

}


@PreviewLightDark
@Composable
private fun HorizontalBoardScreenPreview() {
    KanOneTheme {
        BoardScreen(
            board = previewBoard,
            state = BoardUiState(
                board = previewBoard,
                topBarType = BoardAppBarType.DEFAULT
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()

        )
    }
}

@PreviewLightDark
@Composable
private fun VerticalBoardScreenPreview() {
    KanOneTheme {
        BoardScreen(
            board = previewBoard.copy(isOnListView = true),
            state = BoardUiState(
                board = previewBoard,
                topBarType = BoardAppBarType.DEFAULT
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()

        )
    }
}

private val previewBoard = BoardUi(
    id = 0,
    name = "Board name",
    columns = listOf(
        ColumnUi(
            id = 0,
            name = "Backlog",
            position = 0,
            color = null,
            cards = listOf(
                CardUi(
                    id = 0,
                    title = "Card title",
                    position = 0,
                    color = null,
                    description = "Some description",
                    dueDate = LocalDateTime.now().plusDays(3),
                    createdAt = LocalDateTime.now(),
                    coverFileName = null,
                    tasks = listOf(
                        Task(
                            id = 0,
                            description = "Example",
                            isCompleted = false,
                            position = 0
                        ),
                        Task(
                            id = 1,
                            description = "Completed task",
                            isCompleted = true,
                            position = 1
                        )
                    ),
                    attachments = emptyList(),
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = null
                        ),
                        Label(
                            id = 1,
                            name = "Another label",
                            color = -4221
                        )
                    ),
                    coordinates = Coordinates()
                )
            )
        ),
        ColumnUi(
            id = 1,
            name = "Doing",
            position = 1,
            color = -4466,
            cards = listOf(
                CardUi(
                    id = 0,
                    title = "Card Title",
                    position = 0,
                    color = null,
                    description = "Some description",
                    dueDate = LocalDateTime.now().plusDays(3),
                    createdAt = LocalDateTime.now(),
                    coverFileName = null,
                    tasks = listOf(
                        Task(
                            id = 0,
                            description = "Example",
                            isCompleted = true,
                            position = 0
                        ),
                        Task(
                            id = 1,
                            description = "Completed task",
                            isCompleted = true,
                            position = 1
                        )
                    ),
                    attachments = emptyList(),
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = null
                        )
                    ),
                    coordinates = Coordinates()
                ),
                CardUi(
                    id = 1,
                    title = "Card Title",
                    position = 1,
                    color = null,
                    description = "Some description",
                    dueDate = LocalDateTime.now().plusDays(3),
                    createdAt = LocalDateTime.now(),
                    coverFileName = null,
                    tasks = listOf(
                        Task(
                            id = 0,
                            description = "Example",
                            isCompleted = true,
                            position = 0
                        ),
                        Task(
                            id = 1,
                            description = "Completed task",
                            isCompleted = true,
                            position = 1
                        )
                    ),
                    attachments = emptyList(),
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = null
                        )
                    ),
                    coordinates = Coordinates()
                ),
                CardUi(
                    id = 2,
                    title = "Card Title",
                    position = 2,
                    color = null,
                    description = "Some description",
                    dueDate = LocalDateTime.now().plusDays(3),
                    createdAt = LocalDateTime.now(),
                    coverFileName = null,
                    tasks = listOf(
                        Task(
                            id = 0,
                            description = "Example",
                            isCompleted = true,
                            position = 0
                        ),
                        Task(
                            id = 1,
                            description = "Completed task",
                            isCompleted = true,
                            position = 1
                        )
                    ),
                    attachments = emptyList(),
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = null
                        )
                    ),
                    coordinates = Coordinates()
                ),
                CardUi(
                    id = 3,
                    title = "Card Title",
                    position = 3,
                    color = null,
                    description = "Some description",
                    dueDate = LocalDateTime.now().plusDays(3),
                    createdAt = LocalDateTime.now(),
                    coverFileName = null,
                    tasks = listOf(
                        Task(
                            id = 0,
                            description = "Example",
                            isCompleted = true,
                            position = 0
                        ),
                        Task(
                            id = 1,
                            description = "Completed task",
                            isCompleted = true,
                            position = 1
                        )
                    ),
                    attachments = emptyList(),
                    labels = listOf(
                        Label(
                            id = 0,
                            name = "Label",
                            color = null
                        )
                    ),
                    coordinates = Coordinates()
                ),

                )
        ),
    )
)