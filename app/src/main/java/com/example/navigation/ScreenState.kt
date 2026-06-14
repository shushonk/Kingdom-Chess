package com.example.navigation

sealed class ScreenState {
    object Splash : ScreenState()
    object Home : ScreenState()
    object PlayOffline : ScreenState()
    object PlayVsComputer : ScreenState()
    object Puzzles : ScreenState()
    object Learn : ScreenState()
    object Analysis : ScreenState()
    object Settings : ScreenState()
    object Roadmap : ScreenState()
}
