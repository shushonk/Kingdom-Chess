package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navigation.ScreenState
import com.example.presentation.ChessViewModel
import com.example.presentation.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: ChessViewModel = viewModel()
      
      // Hook up settings state directly to theme config
      MyApplicationTheme(
        darkTheme = viewModel.isDarkMode,
        dynamicColor = false // Force standard custom theme colors for consistent brand experience
      ) {
        val currentScreen by viewModel.currentScreen.collectAsState()

        val backgroundBrush = if (viewModel.isDarkMode) {
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFF0F0F12),
              Color(0xFF1B1B22)
            )
          )
        } else {
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFFF7F8FA),
              Color(0xFFE9ECF0)
            )
          )
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
        ) {
          Scaffold(
            containerColor = Color.Transparent,
            // Notch padding and bottom navigation safe area handling
            modifier = Modifier
              .fillMaxSize()
              .windowInsetsPadding(WindowInsets.safeDrawing)
          ) { innerPadding ->
            // Implement clean, high-fidelity slider transitions under 300ms
            AnimatedContent(
              targetState = currentScreen,
              transitionSpec = {
                (slideInHorizontally { width -> width / 4 } + fadeIn(animationSpec = tween(250))) togetherWith
                (slideOutHorizontally { width -> -width / 4 } + fadeOut(animationSpec = tween(250)))
              },
              label = "ScreenTransition",
              modifier = Modifier.padding(innerPadding)
            ) { targetState ->
              when (targetState) {
                is ScreenState.Splash -> SplashScreen()
                is ScreenState.Home -> HomeScreen(viewModel = viewModel)
                is ScreenState.PlayOffline -> PlayOfflineScreen(viewModel = viewModel)
                is ScreenState.PlayVsComputer -> PlayVsComputerScreen(viewModel = viewModel)
                is ScreenState.Puzzles -> PuzzlesScreen(viewModel = viewModel)
                is ScreenState.Learn -> LearnScreen(viewModel = viewModel)
                is ScreenState.Analysis -> AnalysisScreen(viewModel = viewModel)
                is ScreenState.Settings -> SettingsScreen(viewModel = viewModel)
              }
            }
          }
        }
      }
    }
  }
}

