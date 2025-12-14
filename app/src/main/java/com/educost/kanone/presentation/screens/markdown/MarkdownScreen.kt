package com.educost.kanone.presentation.screens.markdown

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.educost.kanone.presentation.theme.KanOneTheme
import com.educost.kanone.presentation.util.MarkdownRenderer
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun MarkdownScreen(modifier: Modifier = Modifier) {

    val textFieldState = rememberTextFieldState()
    val markdownState = rememberMarkdownState("")
    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isEditing) {
                BasicTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    state = textFieldState,
                )
            } else {
                MarkdownRenderer(
                    markdownState = markdownState,
                )
            }

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

@PreviewLightDark
@Composable
private fun MarkdownScreenPreview() {
    KanOneTheme {
        MarkdownScreen()
    }
}

