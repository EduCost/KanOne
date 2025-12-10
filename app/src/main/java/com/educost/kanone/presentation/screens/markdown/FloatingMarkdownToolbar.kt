@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.educost.kanone.presentation.screens.markdown

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.ModeEdit
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R


@Composable
fun FloatingMarkdownToolbar(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnorderedListClick: () -> Unit,
    onTitleClick: () -> Unit,
    onEditClick: () -> Unit
) {

    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = isExpanded,
        floatingActionButton = {
            ToolbarFAB(isExpanded = isExpanded, onClick = { onEditClick() })
        }
    ) {
        ToolbarButton(
            imageVector = Icons.Rounded.FormatBold,
            contentDescription = stringResource(id = R.string.markdown_button_bold_content_description),
            onClick = onBoldClick
        )
        ToolbarButton(
            imageVector = Icons.Rounded.FormatItalic,
            contentDescription = stringResource(id = R.string.markdown_button_italic_content_description),
            onClick = onItalicClick
        )
        ToolbarButton(
            imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted,
            contentDescription = stringResource(id = R.string.markdown_button_unordered_list_content_description),
            onClick = onUnorderedListClick
        )
        ToolbarButton(
            imageVector = Icons.Rounded.Title,
            contentDescription = stringResource(id = R.string.markdown_button_title_content_description),
            onClick = onTitleClick
        )
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
private fun ToolbarButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}