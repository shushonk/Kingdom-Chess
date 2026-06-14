package com.example.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.*
import com.example.navigation.ScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BoardTheme(
    val themeName: String,
    val lightSquareHex: String,
    val darkSquareHex: String,
    val borderHex: String
) {
    CLASSIC("Classic Walnut", "#F0D9B5", "#B58863", "#7A583A"),
    EMERALD("Emerald Sage", "#ECECD7", "#739552", "#486B2C"),
    OCEAN("Ocean Tempest", "#D6E5F5", "#4B779A", "#2C4F6A"),
    MIDNIGHT("Midnight Obsidian", "#E2E4E6", "#708090", "#3E4953")
}

class ChessViewModel : ViewModel() {

    // Navigation State
    private val _currentScreen = MutableStateFlow<ScreenState>(ScreenState.Splash)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    // App Preferences (In-Memory, simple and robust)
    var isDarkMode by mutableStateOf(true)
    var activeTheme by mutableStateOf(BoardTheme.EMERALD)
    var soundEnabled by mutableStateOf(true)
    var vibrationEnabled by mutableStateOf(true)
    var legalHintsEnabled by mutableStateOf(true)
    var autoPromotionEnabled by mutableStateOf(true)

    // ————— Mode 1: Play Offline State —————
    private val _offlineGameState = MutableStateFlow(ChessGameState())
    val offlineGameState: StateFlow<ChessGameState> = _offlineGameState.asStateFlow()

    var offlineSelectedSquare by mutableStateOf<Position?>(null)
        private set
    var offlineLegalDestinations by mutableStateOf<List<Position>>(emptyList())
        private set

    // ————— Mode 2: Play VS Computer State —————
    private val _aiGameState = MutableStateFlow(ChessGameState())
    val aiGameState: StateFlow<ChessGameState> = _aiGameState.asStateFlow()

    var aiSelectedSquare by mutableStateOf<Position?>(null)
        private set
    var aiLegalDestinations by mutableStateOf<List<Position>>(emptyList())
        private set

    var computerDifficulty by mutableStateOf(Difficulty.MEDIUM)
        private set
    var isAiThinking by mutableStateOf(false)
        private set

    // ————— Mode 3: Puzzles State —————
    var currentPuzzleIndex by mutableStateOf(0)
        private set
    private val _puzzleGameState = MutableStateFlow(ChessGameState(board = PuzzleRepository.puzzles[0].initialBoardSetup()))
    val puzzleGameState: StateFlow<ChessGameState> = _puzzleGameState.asStateFlow()

    var puzzleSelectedSquare by mutableStateOf<Position?>(null)
        private set
    var puzzleLegalDestinations by mutableStateOf<List<Position>>(emptyList())
        private set

    var puzzleCompleted by mutableStateOf(false)
        private set
    var puzzleFeedback by mutableStateOf<String?>(null)
        private set

    // ————— Mode 4: Analysis State —————
    private val _analysisGameState = MutableStateFlow(ChessGameState())
    val analysisGameState: StateFlow<ChessGameState> = _analysisGameState.asStateFlow()

    var analysisSelectedSquare by mutableStateOf<Position?>(null)
        private set
    var analysisLegalDestinations by mutableStateOf<List<Position>>(emptyList())
        private set


    init {
        // Automatically transition from Splash to Home after 2 seconds
        viewModelScope.launch {
            delay(2000)
            _currentScreen.value = ScreenState.Home
        }
    }

    // Navigation triggers
    fun navigateTo(screen: ScreenState) {
        _currentScreen.value = screen
        
        // Reset selections when changing screens
        clearAllSelections()

        // Sync or reset boards for certain entries
        if (screen is ScreenState.Puzzles) {
            loadPuzzle(currentPuzzleIndex)
        }
    }

    private fun clearAllSelections() {
        offlineSelectedSquare = null
        offlineLegalDestinations = emptyList()
        aiSelectedSquare = null
        aiLegalDestinations = emptyList()
        puzzleSelectedSquare = null
        puzzleLegalDestinations = emptyList()
        analysisSelectedSquare = null
        analysisLegalDestinations = emptyList()
    }

    // Settings adjustments
    fun toggleDarkMode() { isDarkMode = !isDarkMode }
    fun selectTheme(theme: BoardTheme) { activeTheme = theme }
    fun toggleSound() { soundEnabled = !soundEnabled }
    fun toggleVibration() { vibrationEnabled = !vibrationEnabled }
    fun toggleLegalHints() { legalHintsEnabled = !legalHintsEnabled }
    fun toggleAutoPromotion() { autoPromotionEnabled = !autoPromotionEnabled }

    // ———————————————————————————————————————————
    // OFFLINE GAMEPLAY ACTIONS
    // ———————————————————————————————————————————
    fun handleOfflineSquareTap(pos: Position) {
        val state = _offlineGameState.value
        if (state.status != GameStatus.ACTIVE) return

        val selected = offlineSelectedSquare
        if (selected == null) {
            // Select piece of active color
            val piece = state.board[pos.row][pos.col]
            if (piece != null && piece.color == state.activeColor) {
                offlineSelectedSquare = pos
                offlineLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
            }
        } else {
            // We already had a selected piece
            if (pos in offlineLegalDestinations) {
                // Execute move
                val moves = ChessEngine.getLegalMoves(state.board, selected, state)
                val matchingMove = moves.firstOrNull { it.to == pos }
                if (matchingMove != null) {
                    _offlineGameState.value = ChessEngine.makeMove(state, matchingMove)
                }
                offlineSelectedSquare = null
                offlineLegalDestinations = emptyList()
            } else {
                // Target is not a legal move destination
                val piece = state.board[pos.row][pos.col]
                if (piece != null && piece.color == state.activeColor) {
                    // Switch selection to another friendly piece
                    offlineSelectedSquare = pos
                    offlineLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
                } else {
                    // Deselect
                    offlineSelectedSquare = null
                    offlineLegalDestinations = emptyList()
                }
            }
        }
    }

    fun restartOfflineGame() {
        _offlineGameState.value = ChessGameState()
        clearAllSelections()
    }

    // ———————————————————————————————————————————
    // AI GAMEPLAY ACTIONS
    // ———————————————————————————————————————————
    fun setAiDifficulty(difficulty: Difficulty) {
        computerDifficulty = difficulty
    }

    fun handleAiSquareTap(pos: Position) {
        if (isAiThinking) return
        val state = _aiGameState.value
        if (state.status != GameStatus.ACTIVE) return

        // Player is always WHITE in this mode. Computer is BLACK.
        if (state.activeColor != ChessColor.WHITE) return

        val selected = aiSelectedSquare
        if (selected == null) {
            val piece = state.board[pos.row][pos.col]
            if (piece != null && piece.color == ChessColor.WHITE) {
                aiSelectedSquare = pos
                aiLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
            }
        } else {
            if (pos in aiLegalDestinations) {
                val moves = ChessEngine.getLegalMoves(state.board, selected, state)
                val matchingMove = moves.firstOrNull { it.to == pos }
                if (matchingMove != null) {
                    val nextState = ChessEngine.makeMove(state, matchingMove)
                    _aiGameState.value = nextState
                    
                    aiSelectedSquare = null
                    aiLegalDestinations = emptyList()

                    // If game still active, trigger computer play
                    if (nextState.status == GameStatus.ACTIVE && nextState.activeColor == ChessColor.BLACK) {
                        triggerComputerMove()
                    }
                }
            } else {
                val piece = state.board[pos.row][pos.col]
                if (piece != null && piece.color == ChessColor.WHITE) {
                    aiSelectedSquare = pos
                    aiLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
                } else {
                    aiSelectedSquare = null
                    aiLegalDestinations = emptyList()
                }
            }
        }
    }

    private fun triggerComputerMove() {
        isAiThinking = true
        viewModelScope.launch {
            // Simulate realistic human thinking delay
            delay(1000)
            
            val currentState = _aiGameState.value
            val aiMove = ChessAI.chooseMove(currentState, computerDifficulty)
            
            if (aiMove != null) {
                _aiGameState.value = ChessEngine.makeMove(currentState, aiMove)
            }
            isAiThinking = false
        }
    }

    fun restartAiGame() {
        _aiGameState.value = ChessGameState()
        isAiThinking = false
        clearAllSelections()
    }

    // ———————————————————————————————————————————
    // PUZZLE ACTIONS
    // ———————————————————————————————————————————
    fun loadPuzzle(index: Int) {
        val puzzleList = PuzzleRepository.puzzles
        if (index in puzzleList.indices) {
            currentPuzzleIndex = index
            val puzzle = puzzleList[index]
            _puzzleGameState.value = ChessGameState(
                board = puzzle.initialBoardSetup(),
                activeColor = puzzle.activeColor,
                status = GameStatus.ACTIVE,
                isCheck = false,
                history = emptyList()
            )
            puzzleSelectedSquare = null
            puzzleLegalDestinations = emptyList()
            puzzleCompleted = false
            puzzleFeedback = "Your turn: Find the absolute best move!"
        }
    }

    fun loadNextPuzzle() {
        val count = PuzzleRepository.puzzles.size
        val nextIndex = (currentPuzzleIndex + 1) % count
        loadPuzzle(nextIndex)
    }

    fun handlePuzzleSquareTap(pos: Position) {
        if (puzzleCompleted) return
        val state = _puzzleGameState.value
        val puzzle = PuzzleRepository.puzzles[currentPuzzleIndex]

        val selected = puzzleSelectedSquare
        if (selected == null) {
            val piece = state.board[pos.row][pos.col]
            if (piece != null && piece.color == state.activeColor) {
                puzzleSelectedSquare = pos
                puzzleLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
            }
        } else {
            if (pos in puzzleLegalDestinations) {
                // User attempted to play this move
                val moves = ChessEngine.getLegalMoves(state.board, selected, state)
                val matchingMove = moves.firstOrNull { it.to == pos }
                
                if (matchingMove != null) {
                    // Check if it's correct!
                    if (selected == puzzle.correctMoveFrom && pos == puzzle.correctMoveTo) {
                        // Success! Update board & release completed feedback
                        _puzzleGameState.value = ChessEngine.makeMove(state, matchingMove)
                        puzzleCompleted = true
                        puzzleFeedback = puzzle.resultMessage
                    } else {
                        // Incorrect move!
                        puzzleFeedback = "❌ Incorrect move. That isn't the solution. Try again!"
                    }
                }
                puzzleSelectedSquare = null
                puzzleLegalDestinations = emptyList()
            } else {
                val piece = state.board[pos.row][pos.col]
                if (piece != null && piece.color == state.activeColor) {
                    puzzleSelectedSquare = pos
                    puzzleLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
                } else {
                    puzzleSelectedSquare = null
                    puzzleLegalDestinations = emptyList()
                }
            }
        }
    }

    // ———————————————————————————————————————————
    // ANALYSIS ACTIONS
    // ———————————————————————————————————————————
    fun handleAnalysisSquareTap(pos: Position) {
        val state = _analysisGameState.value
        val selected = analysisSelectedSquare
        if (selected == null) {
            val piece = state.board[pos.row][pos.col]
            if (piece != null && piece.color == state.activeColor) {
                analysisSelectedSquare = pos
                analysisLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
            }
        } else {
            if (pos in analysisLegalDestinations) {
                val moves = ChessEngine.getLegalMoves(state.board, selected, state)
                val matchingMove = moves.firstOrNull { it.to == pos }
                if (matchingMove != null) {
                    _analysisGameState.value = ChessEngine.makeMove(state, matchingMove)
                }
                analysisSelectedSquare = null
                analysisLegalDestinations = emptyList()
            } else {
                val piece = state.board[pos.row][pos.col]
                if (piece != null && piece.color == state.activeColor) {
                    analysisSelectedSquare = pos
                    analysisLegalDestinations = ChessEngine.getLegalMoves(state.board, pos, state).map { it.to }
                } else {
                    analysisSelectedSquare = null
                    analysisLegalDestinations = emptyList()
                }
            }
        }
    }

    fun restartAnalysisGame() {
        _analysisGameState.value = ChessGameState()
        clearAllSelections()
    }
}
