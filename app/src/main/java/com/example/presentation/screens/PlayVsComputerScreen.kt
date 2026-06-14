package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ChessColor
import com.example.domain.Difficulty
import com.example.domain.GameStatus
import com.example.navigation.ScreenState
import com.example.presentation.ChessViewModel
import com.example.presentation.components.ChessBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayVsComputerScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.aiGameState.collectAsState()
    val isThinking = viewModel.isAiThinking
    val activeDifficulty = viewModel.computerDifficulty
    val theme = viewModel.activeTheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vs Computer AI", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.Home) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.restartAiGame() },
                        modifier = Modifier.testTag("reset_ai_game")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset match")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // ————— DIFFICULTY SELECTOR & AI BANNER —————
            DifficultySelectorRow(
                selectedDifficulty = activeDifficulty,
                onDifficultySelect = { viewModel.setAiDifficulty(it) },
                enabled = gameState.history.isEmpty() && !isThinking,
                modifier = Modifier.fillMaxWidth()
            )

            // Top captures (Black's captures, i.e., pieces captured by WHITE user)
            CapturesRow(
                capturedList = gameState.capturedPiecesWhite,
                colorLabel = "AI's losses",
                modifier = Modifier.fillMaxWidth()
            )

            // STATUS NOTIFICATION OR AI LOADER
            AiStatusIndicator(
                isThinking = isThinking,
                isCheck = gameState.isCheck,
                status = gameState.status,
                activeTurnColor = gameState.activeColor
            )

            // ————— CHESS BOARD —————
            ChessBoard(
                board = gameState.board,
                selectedSquare = viewModel.aiSelectedSquare,
                legalDestinations = viewModel.aiLegalDestinations,
                activeTheme = theme,
                isCheck = gameState.isCheck,
                activeColor = gameState.activeColor,
                onSquareClick = { pos -> viewModel.handleAiSquareTap(pos) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            // Bottom captures (White's captures, i.e., pieces captured by BLACK AI)
            CapturesRow(
                capturedList = gameState.capturedPiecesBlack,
                colorLabel = "Your losses",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // MOVE HISTORY
            MoveHistoryPanel(
                history = gameState.history,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )

            // GAME END OVERLAY
            if (gameState.status != GameStatus.ACTIVE) {
                GameEndDialog(
                    status = gameState.status,
                    winnerColor = gameState.activeColor.opponent(),
                    onPlayAgain = { viewModel.restartAiGame() }
                )
            }
        }
    }
}

@Composable
fun DifficultySelectorRow(
    selectedDifficulty: Difficulty,
    onDifficultySelect: (Difficulty) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ENGINE TIER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Difficulty.values().forEach { d ->
                    val isSelected = d == selectedDifficulty
                    val baseColor = when (d) {
                        Difficulty.EASY -> Color(0xFF00C853)
                        Difficulty.MEDIUM -> Color(0xFFFF9100)
                        Difficulty.HARD -> Color(0xFFFF1744)
                    }
                    
                    val bg = if (isSelected) baseColor else baseColor.copy(alpha = 0.15f)
                    val tc = if (isSelected) Color.White else baseColor

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable(enabled = enabled) { onDifficultySelect(d) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = d.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = tc
                        )
                    }
                }
            }
            if (!enabled) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Difficulty locked while match is active.",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun AiStatusIndicator(
    isThinking: Boolean,
    isCheck: Boolean,
    status: GameStatus,
    activeTurnColor: ChessColor
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isThinking,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { thinking ->
            if (thinking) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2C4F6A))
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFD6E5F5),
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Nova AI is computing...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                val turnLabel = if (activeTurnColor == ChessColor.WHITE) "Your Turn (White)" else "AI is playing..."
                val bgBadge = if (activeTurnColor == ChessColor.WHITE) Color(0xFFE2F0D9) else Color(0xFF2C2C2C)
                val txColor = if (activeTurnColor == ChessColor.WHITE) Color(0xFF385723) else Color.White

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isCheck) Color(0x22EF5350) else bgBadge)
                        .border(
                            1.dp,
                            if (isCheck) Color(0xFFEF5350) else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isCheck) "⚠️ ROYAL THREAT - CHECK!" else turnLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCheck) Color(0xFFEF5350) else txColor
                    )
                }
            }
        }
    }
}
