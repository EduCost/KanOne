package com.educost.kanone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.educost.kanone.presentation.navigation.AppEntry
import com.educost.kanone.presentation.navigation.AppEntryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val entryViewModel by viewModels<AppEntryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            entryViewModel.themeData.value == null
        }
        enableEdgeToEdge()
        setContent {
            AppEntry(viewModel = entryViewModel)
        }
    }
}