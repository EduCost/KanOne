package com.educost.kanone.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes

@Composable
fun MarkdownRenderer(
    modifier: Modifier = Modifier,
    markdownState: MarkdownState,
    typography: MarkdownTypography = markdownTypography()
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
            checkbox = { MarkdownCheckBox(it.content, it.node, it.typography.text) }
        ),
        typography = typography
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