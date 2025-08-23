package com.educost.kanone.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionTopBar(
    modifier: Modifier = Modifier,
    title: String,
    leftIcon: ImageVector = Icons.Filled.Clear,
    leftIconContentDescription: String,
    onLeftIconClick: () -> Unit,
    rightIcon: ImageVector = Icons.Filled.Check,
    rightIconContentDescription: String,
    onRightIconClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        title = {
            Text(
                text = title,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onLeftIconClick
            ) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = leftIconContentDescription
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRightIconClick
            ) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = rightIconContentDescription
                )
            }
        },
    )
}