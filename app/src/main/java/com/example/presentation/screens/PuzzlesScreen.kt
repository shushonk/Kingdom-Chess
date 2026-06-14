package com.example.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import com.example.domain.PuzzleRepository
import com.example.navigation.ScreenState
import com.example.presentation.ChessViewModel
import com.example.presentation.components.ChessBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzlesScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.puzzleGameState.collectAsState()
    val theme = viewModel.activeTheme
    val completed = viewModel.puzzleCompleted
    val feedback = viewModel.puzzleFeedback
    val activePuzzleIndex = viewModel.currentPuzzleIndex
    val puzzle = PuzzleRepository.puzzles[activePuzzleIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tactical Puzzles", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.Home) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadPuzzle(activePuzzleIndex) },
                        modifier = Modifier.testTag("reset_puzzle")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry Puzzle")
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
            
            // ————— PROGRESS HEADER —————
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PUZZLE ${activePuzzleIndex + 1} OF ${PuzzleRepository.puzzles.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = puzzle.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // ————— PUZZLE PROMPT CARD —————
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "GOAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = puzzle.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ————— REACTION FEEDBACK RIBBON —————
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (feedback != null) {
                    val containerCol = if (completed) Color(0xFF1B5E20) else Color(0x30D32F2F)
                    val fontColor = if (completed) Color.White else Color(0xFFEF5350)
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(containerCol)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = feedback,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = fontColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ————— CHESS BOARD —————
            ChessBoard(
                board = gameState.board,
                selectedSquare = viewModel.puzzleSelectedSquare,
                legalDestinations = viewModel.puzzleLegalDestinations,
                activeTheme = theme,
                isCheck = gameState.isCheck,
                activeColor = gameState.activeColor,
                onSquareClick = { pos -> viewModel.handlePuzzleSquareTap(pos) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            // ————— ACTION AREA & NEXT LEVEL CONTROL —————
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.loadPuzzle(activePuzzleIndex) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retry", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { viewModel.loadNextPuzzle() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (completed) Color(0xFF739552) else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1.2f).testTag("next_puzzle_button")
                ) {
                    Text(if (completed) "Next Puzzle" else "Skip Puzzle", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
