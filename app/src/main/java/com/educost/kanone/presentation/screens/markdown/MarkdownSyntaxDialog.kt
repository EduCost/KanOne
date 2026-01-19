package com.educost.kanone.presentation.screens.markdown

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.educost.kanone.R
import com.educost.kanone.presentation.util.MarkdownRenderer
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun MarkdownSyntaxDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.markdown_syntax_dialog_close))
            }
        },
        title = {
            Text(text = stringResource(R.string.markdown_syntax_dialog_title))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeadingSyntax()
                BoldSyntax()
                ItalicSyntax()
                StrikeThroughSyntax()
                ListSyntax()
                LinkSyntax()
                CodeSyntax()
                QuoteSyntax()
                DividerSyntax()
            }
        }
    )
}

@Composable
private fun HeadingSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_headings),
        labelStyle = MaterialTheme.typography.titleLarge,
        syntaxCode = stringResource(R.string.markdown_syntax_code_headings)
    )
}

@Composable
private fun BoldSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_bold),
        labelStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        syntaxCode = stringResource(R.string.markdown_syntax_code_bold)
    )
}

@Composable
private fun ItalicSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_italic),
        labelStyle = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
        syntaxCode = stringResource(R.string.markdown_syntax_code_italic)
    )
}

@Composable
private fun StrikeThroughSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_strike_through),
        labelStyle = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough),
        syntaxCode = stringResource(R.string.markdown_syntax_code_strike_through)
    )
}

@Composable
private fun ListSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_lists),
        syntaxCode = stringResource(R.string.markdown_syntax_code_lists)
    )
}

@Composable
private fun LinkSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_links),
        labelStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
        syntaxCode = stringResource(R.string.markdown_syntax_code_links)
    )
}

@Composable
private fun CodeSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_code),
        labelStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
        syntaxCode = stringResource(R.string.markdown_syntax_code_code)
    )
}

@Composable
private fun QuoteSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_quote),
        syntaxCode = stringResource(R.string.markdown_syntax_code_quote)
    )
}

@Composable
private fun DividerSyntax() {
    BaseSyntaxCard(
        label = stringResource(R.string.markdown_syntax_label_divider),
        syntaxCode = stringResource(R.string.markdown_syntax_code_divider)
    )
}

@Composable
private fun BaseSyntaxCard(
    label: String,
    syntaxCode: String,
    labelStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    var isExpanded by remember { mutableStateOf(false) }
    val markdownState = rememberMarkdownState(syntaxCode)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = labelStyle
            )

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = stringResource(R.string.markdown_syntax_section_syntax),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = syntaxCode,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineBreak = LineBreak.Paragraph
                        ),
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(R.string.markdown_syntax_section_preview),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MarkdownRenderer(
                        markdownState = markdownState
                    )
                }
            }
        }
    }
}
