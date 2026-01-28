@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.educost.kanone.presentation.screens.markdown

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.ModeEdit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.StrikethroughS
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.educost.kanone.R
import com.educost.kanone.presentation.util.UiText


private data class MarkdownButton(
    val imageVector: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)

@Composable
fun FloatingMarkdownToolbar(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnorderedListClick: () -> Unit,
    onTitleClick: () -> Unit,
    onStrikethroughClick: () -> Unit,
    onCodeClick: () -> Unit,
    onQuoteClick: () -> Unit,
    onInfoClick: () -> Unit,
    onEditClick: () -> Unit
) {

    val markdownIcon = ImageVector.vectorResource(R.drawable.markdown_logo)
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val toolbarItems = remember { listOf(
        MarkdownButton(
            imageVector = Icons.Rounded.FormatBold,
            contentDescription = UiText.StringResource(R.string.markdown_button_bold_content_description).asString(context),
            onClick = onBoldClick
        ),
        MarkdownButton(
            imageVector = Icons.Rounded.FormatItalic,
            contentDescription = UiText.StringResource(R.string.markdown_button_italic_content_description).asString(context),
            onClick = onItalicClick
        ),
        MarkdownButton(
            imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted,
            contentDescription = UiText.StringResource(R.string.markdown_button_unordered_list_content_description).asString(context),
            onClick = onUnorderedListClick
        ),
    )}

    val dropdownItems = remember { listOf(
        MarkdownButton(
            imageVector = Icons.Rounded.Title,
            contentDescription = UiText.StringResource(R.string.markdown_button_title_content_description).asString(context),
            onClick = onTitleClick
        ),
        MarkdownButton(
            imageVector = Icons.Rounded.StrikethroughS,
            contentDescription = UiText.StringResource(R.string.markdown_button_strikethrough_content_description).asString(context),
            onClick = onStrikethroughClick
        ),
        MarkdownButton(
            imageVector = Icons.Rounded.Code,
            contentDescription = UiText.StringResource(R.string.markdown_button_code_content_description).asString(context),
            onClick = onCodeClick
        ),
        MarkdownButton(
            imageVector = Icons.Rounded.FormatQuote,
            contentDescription = UiText.StringResource(R.string.markdown_button_quote_content_description).asString(context),
            onClick = onQuoteClick
        ),
        MarkdownButton(
            imageVector = markdownIcon,
            contentDescription = UiText.StringResource(R.string.markdown_button_markdown_syntax).asString(context),
            onClick = onInfoClick
        )
    ) }

    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = isExpanded,
        floatingActionButton = {

            ToolbarFAB(isExpanded = isExpanded, onClick = { onEditClick() })

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                dropdownItems.forEach { dropdownItem ->
                    ToolbarDropdownItem(toolbarItem = dropdownItem)
                }
            }
        }
    ) {

        toolbarItems.forEach { toolbarItem ->
            ToolbarButton(toolbarItem = toolbarItem)
        }

        ToolbarButton(MarkdownButton(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = UiText.StringResource(R.string.markdown_button_more_options).asString(context),
            onClick = { isDropdownExpanded = !isDropdownExpanded }
        ))
    }
}


@Composable
private fun ToolbarFAB(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    FloatingToolbarDefaults.StandardFloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = if (isExpanded) Icons.AutoMirrored.Rounded.MenuBook else Icons.Rounded.ModeEdit,
            contentDescription = null
        )
    }
}

@Composable
private fun ToolbarButton(toolbarItem: MarkdownButton) {
    IconButton(
        onClick = toolbarItem.onClick,
    ) {
        Icon(
            imageVector = toolbarItem.imageVector,
            contentDescription = toolbarItem.contentDescription
        )
    }
}

@Composable
private fun ToolbarDropdownItem(toolbarItem: MarkdownButton) {
    DropdownMenuItem(
        text = {
            Text(text = toolbarItem.contentDescription)
        },
        onClick = toolbarItem.onClick,
        leadingIcon = {
            Icon(
                imageVector = toolbarItem.imageVector,
                contentDescription = null
            )
        }
    )
}