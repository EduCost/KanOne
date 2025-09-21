package com.educost.kanone.presentation.screens.settings.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.educost.kanone.R
import com.educost.kanone.presentation.components.NavigateBackIcon
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(modifier: Modifier = Modifier, onNavigateBack: () -> Unit) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_credits)) },
                navigationIcon = { NavigateBackIcon(onClick = onNavigateBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        val libraries by produceLibraries(R.raw.aboutlibraries)

        LibrariesContainer(libraries, Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
        )
    }
}