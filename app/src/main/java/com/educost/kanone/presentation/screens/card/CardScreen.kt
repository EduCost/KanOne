package com.educost.kanone.presentation.screens.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.R
import com.educost.kanone.domain.model.CardItem
import com.educost.kanone.presentation.screens.card.components.CardAppBar
import com.educost.kanone.presentation.screens.card.components.CardAttachments
import com.educost.kanone.presentation.screens.card.components.CardDateCreated
import com.educost.kanone.presentation.screens.card.components.CardDescription
import com.educost.kanone.presentation.screens.card.components.CardDueDate
import com.educost.kanone.presentation.screens.card.components.CardLabels
import com.educost.kanone.presentation.screens.card.components.CardTasks
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CardScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel(),
    cardId: Long
) {
    LaunchedEffect(Unit) {
        viewModel.onIntent(CardIntent.ObserveCard(cardId))
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(flow = viewModel.sideEffectFlow) { event ->
        when (event) {
            is CardSideEffect.ShowSnackBar -> {
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

    CardScreen(
        modifier = modifier,
        state = state,
        onIntent = viewModel::onIntent,
        snackBarHostState = snackBarHostState
    )
}

@Composable
private fun CardScreen(
    modifier: Modifier = Modifier,
    state: CardUiState,
    onIntent: (CardIntent) -> Unit,
    snackBarHostState: SnackbarHostState
) {

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            state.card?.let { card ->

                CardAppBar(
                    card = card,
                    onIntent = onIntent
                )
            }
        }
    ) { innerPadding ->
        state.card?.let { card ->

            val createdAt by remember {
                mutableStateOf(
                    state.card.createdAt
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
            }

            val dueDate by remember {
                mutableStateOf(
                    state.card.dueDate
                        ?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
            }

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sample_image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                )

                Spacer(Modifier.height(24.dp))


                CardDescription(
                    cardDescription = card.description ?: ""
                )

                Spacer(Modifier.height(24.dp))

                CardTasks()

                Spacer(Modifier.height(24.dp))

                Row {
                    CardDateCreated(
                        modifier = Modifier.weight(1f),
                        createdAt = createdAt
                    )

                    Spacer(Modifier.width(8.dp))

                    CardDueDate(
                        modifier = Modifier.weight(1f),
                        dueDate = dueDate
                    )
                }

                Spacer(Modifier.height(24.dp))

                CardAttachments()

                Spacer(Modifier.height(24.dp))

                CardLabels()

            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CardScreenPreview() {
    KanOneTheme {
        CardScreen(
            state = CardUiState(
                card = CardItem(
                    id = 0,
                    title = "Card title",
                    position = 0,
                    createdAt = LocalDateTime.now(),
                    description = /*"Card description"*/null,
                    color = null,
                    dueDate = /*LocalDateTime.now().plusDays(3)*/null,
                    thumbnailFileName = null,
                    checklists = emptyList(),
                    attachments = emptyList(),
                    labels = emptyList(),
                )
            ),
            onIntent = {},
            snackBarHostState = SnackbarHostState()
        )
    }
}