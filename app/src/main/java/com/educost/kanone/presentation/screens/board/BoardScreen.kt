package com.educost.kanone.presentation.screens.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
import com.educost.kanone.presentation.screens.board.components.AddColumn
import com.educost.kanone.presentation.screens.board.components.BoardAppBar
import com.educost.kanone.presentation.screens.board.components.BoardColumn
import com.educost.kanone.presentation.screens.board.components.BoardModalBottomSheet
import com.educost.kanone.presentation.screens.board.components.ColumnCard
import com.educost.kanone.presentation.screens.board.model.BoardSizes
import com.educost.kanone.presentation.screens.board.model.BoardUi
import com.educost.kanone.presentation.screens.board.model.CardUi
import com.educost.kanone.presentation.screens.board.model.ColumnUi
import com.educost.kanone.presentation.screens.board.model.Coordinates
import com.educost.kanone.presentation.screens.board.state.BoardState
import com.educost.kanone.presentation.screens.board.utils.BoardAppBarType
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
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


    BoardScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    state: BoardState,
    onIntent: (BoardIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val scrollChange = -panChange.x
        onIntent(BoardIntent.OnZoomChange(zoomChange, scrollChange))
    }

    BackHandler(enabled = state.hasEditStates || state.isOnFullScreen) {
        onIntent(BoardIntent.OnBackPressed)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .transformable(transformableState)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, _ ->
                        onIntent(BoardIntent.OnDrag(change.position))
                    },
                    onDragStart = { offset ->
                        onIntent(BoardIntent.OnDragStart(offset))
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
            state.board?.let { board ->
                BoardAppBar(
                    boardName = board.name,
                    type = state.topBarType,
                    isDropdownMenuExpanded = state.isBoardDropdownMenuExpanded,
                    isFullScreen = state.isOnFullScreen,
                    onIntent = onIntent
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        state.board?.let { board ->

            val contentPadding = remember(state.isOnFullScreen, board.sizes) {
                if (state.isOnFullScreen) board.sizes.columnFullScreenPaddingValues
                else board.sizes.columnPaddingValues
            }

            LazyRow(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        onIntent(
                            BoardIntent.SetBoardCoordinates(
                                coordinates = Coordinates(
                                    position = layoutCoordinates.positionInRoot(),
                                    width = layoutCoordinates.size.width,
                                    height = layoutCoordinates.size.height
                                )
                            )
                        )
                    }
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(board.sizes.columnsSpaceBy),
                state = state.board.listState
            ) {
                itemsIndexed(
                    items = board.columns,
                    key = { index, column -> "${column.id}_$index" }
                ) { index, column ->
                    val isDraggingColumn = state.dragState.draggingColumn?.id == column.id
                    BoardColumn(
                        modifier = Modifier
                            .onGloballyPositioned { layoutCoordinates ->
                                onIntent(
                                    BoardIntent.SetColumnCoordinates(
                                        columnId = column.id,
                                        coordinates = Coordinates(
                                            position = layoutCoordinates.positionInRoot(),
                                            width = layoutCoordinates.size.width,
                                            height = layoutCoordinates.size.height
                                        )
                                    )
                                )
                            }
                            .then(
                                if (isDraggingColumn) {
                                    Modifier
                                        .graphicsLayer {
                                            colorFilter = ColorFilter.tint(Color.Gray)
                                            alpha = 0.05f
                                        }
                                } else {
                                    Modifier
                                }
                            ),
                        column = column,
                        columnIndex = index,
                        state = state,
                        onIntent = onIntent,
                        sizes = board.sizes
                    )
                }

                item {
                    AddColumn(
                        state = state,
                        onIntent = onIntent,
                        sizes = board.sizes
                    )
                }
            }

            if (state.isModalSheetExpanded) {
                BoardModalBottomSheet(
                    board = board,
                    isFullScreen = state.isOnFullScreen,
                    onIntent = onIntent
                )
            }
        }
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
    state.dragState.draggingCard?.let { card ->
        ColumnCard(
            modifier = Modifier
                .graphicsLayer {
                    translationY = state.dragState.itemOffset.y
                    translationX = state.dragState.itemOffset.x
                    rotationZ = 2f
                }
                .width(with(localDensity) { card.coordinates.width.toDp() })
                .height(with(localDensity) { card.coordinates.height.toDp() }),
            card = card,
            showImage = state.board?.showImages ?: true,
            sizes = state.board?.sizes ?: BoardSizes(),
        )
    }
    state.dragState.draggingColumn?.let { column ->
        BoardColumn(
            modifier = Modifier
                .graphicsLayer {
                    translationY = state.dragState.itemOffset.y
                    translationX = state.dragState.itemOffset.x
                    rotationZ = 2f
                }
                .width(with(localDensity) { column.coordinates.width.toDp() })
                .height(with(localDensity) { column.coordinates.height.toDp() }),
            column = column,
            state = BoardState(),
            columnIndex = -1,
            onIntent = { },
            sizes = state.board?.sizes ?: BoardSizes()
        )
    }
}


@PreviewLightDark
@Composable
private fun BoardScreenPreview() {
    KanOneTheme {
        BoardScreen(
            state = BoardState(
                board = BoardUi(
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
                        )
                    )
                ),
                topBarType = BoardAppBarType.DEFAULT
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()

        )
    }
}
