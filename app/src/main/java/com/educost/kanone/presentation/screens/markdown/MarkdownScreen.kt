package com.educost.kanone.presentation.screens.markdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.MarkdownRenderer
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun MarkdownScreen(
    modifier: Modifier = Modifier,
    cardId: Long,
    viewModel: MarkdownViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadMarkdown(cardId)
    }

    val markdown by viewModel.markdown.collectAsStateWithLifecycle()

    markdown?.let { markdown ->
        MarkdownScreen(
            modifier = modifier,
            markdown = markdown,
            onTextChange = { newMarkdown ->
                viewModel.saveMarkdown(cardId, newMarkdown)
            }
        )
    }
}

@Composable
fun MarkdownScreen(
    modifier: Modifier = Modifier,
    markdown: String,
    onTextChange: (String) -> Unit
) {

    var isEditing by rememberSaveable { mutableStateOf(true) }

    val textFieldState = rememberTextFieldState(markdown)
    val markdownState = rememberMarkdownState(textFieldState.text.toString())

    LaunchedEffect(textFieldState.text) {
        onTextChange(textFieldState.text.toString())
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            if (isEditing) MarkdownTextField(textFieldState = textFieldState)
            else MarkdownRenderer(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                markdownState = markdownState
            )

            FloatingMarkdownToolbar(
                modifier = Modifier.align(Alignment.BottomEnd),
                isExpanded = isEditing,
                onBoldClick = { },
                onItalicClick = { },
                onUnorderedListClick = { },
                onTitleClick = { },
                onEditClick = { isEditing = !isEditing }
            )
        }
    }
}

@Composable
private fun MarkdownTextField(modifier: Modifier = Modifier, textFieldState: TextFieldState) {
    BasicTextField(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        state = textFieldState,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
    )
}

@PreviewLightDark
@Composable
private fun MarkdownScreenPreview() {
    KanOneTheme {
        MarkdownScreen(markdown = "Markdown", onTextChange = {})
    }
}

