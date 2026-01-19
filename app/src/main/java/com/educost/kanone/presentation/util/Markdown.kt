package com.educost.kanone.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.educost.kanone.presentation.navigation.LocalThemeData
import com.educost.kanone.presentation.theme.ThemeType
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.elements.MarkdownCheckBox
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.MarkdownState
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.markdownAnimations
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MarkdownRenderer(
    modifier: Modifier = Modifier,
    markdownState: MarkdownState,
    typography: MarkdownTypography = markdownTypographyDefault()
) {

    val themeData = LocalThemeData.current
    val isDarkTheme = when(themeData.themeType) {
        ThemeType.SYSTEM -> isSystemInDarkTheme()
        ThemeType.LIGHT -> false
        ThemeType.DARK -> true
    }

    val highlightsBuilder = remember(isDarkTheme) {
        Highlights.Builder().theme(SyntaxThemes.darcula(darkMode = isDarkTheme))
    }

    Markdown(
        modifier = modifier,
        markdownState = markdownState,
        imageTransformer = Coil3ImageTransformerImpl,
        typography = typography,
        components = markdownComponents(
            codeBlock = {
                MarkdownHighlightedCodeBlock(
                    content = it.content,
                    node = it.node,
                    highlightsBuilder = highlightsBuilder,
                    showHeader = true,
                )
            },
            codeFence = {
                MarkdownHighlightedCodeFence(
                    content = it.content,
                    node = it.node,
                    highlightsBuilder = highlightsBuilder,
                    showHeader = true,
                )
            },
            checkbox = { MarkdownCheckBox(it.content, it.node, it.typography.text) },
        ),
        loading = {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularWavyProgressIndicator()
            }
        },
    )
}

@Composable
fun markdownTypographyDefault(): MarkdownTypography {
    return markdownTypography(
        h1 = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
        h2 = MaterialTheme.typography.displayMedium.copy(fontSize = 34.sp),
        h3 = MaterialTheme.typography.displaySmall.copy(fontSize = 28.sp),
        h4 = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
        h5 = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
        h6 = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
    )
}

@Composable
fun markdownTypographySmall(): MarkdownTypography {
    return markdownTypography(
        h1 = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
        h2 = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
        h3 = MaterialTheme.typography.displaySmall.copy(fontSize = 24.sp),
        h4 = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
        h5 = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
        h6 = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
        text = MaterialTheme.typography.bodyLarge,
        code = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
        inlineCode = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
        quote = MaterialTheme.typography.bodyMedium.plus(SpanStyle(fontStyle = FontStyle.Italic)),
        paragraph = MaterialTheme.typography.bodyLarge,
        ordered = MaterialTheme.typography.bodyLarge,
        bullet = MaterialTheme.typography.bodyLarge,
        list = MaterialTheme.typography.bodyLarge,
        textLink = TextLinkStyles(
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline
            ).toSpanStyle()
        ),
        table = MaterialTheme.typography.bodyLarge
    )
}

fun TextFieldValue.wrapSelection(prefix: String, suffix: String = prefix): TextFieldValue {
    val selection = this.selection
    if (!selection.collapsed) {
        try {
            val textStart = selection.start
            val textEnd = selection.end
            val selectedText = this.text.substring(textStart, textEnd)


            val newText = this.text.replaceRange(
                textStart,
                textEnd,
                "$prefix$selectedText$suffix"
            )

            return this.copy(
                text = newText,
                selection = TextRange(textStart, textEnd + prefix.length + suffix.length)
            )


        } catch (e: Exception) {
            println(e)
        }
    } else {
        val newText = this.text.replaceRange(
            startIndex = selection.start,
            endIndex = selection.end,
            replacement = "$prefix$suffix"
        )

        val newCursorPosition = selection.start + prefix.length

        return this.copy(
            text = newText,
            selection = TextRange(newCursorPosition, newCursorPosition)
        )
    }

    return this
}

fun TextFieldValue.addPrefixToCurrentLine(prefix: String): TextFieldValue {
    val cursorPosition = this.selection.start
    val text = this.text

    val lineStart = text.lastIndexOf(
        char = '\n',
        startIndex = (cursorPosition - 1).coerceAtLeast(0)
    ) + 1

    val newText = text.substring(0, lineStart) + prefix + text.substring(lineStart)
    val newCursorPosition = cursorPosition + prefix.length

    return this.copy(
        text = newText,
        selection = TextRange(newCursorPosition)
    )
}
