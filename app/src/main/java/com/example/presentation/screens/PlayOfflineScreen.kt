package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ChessColor
import com.example.domain.GameStatus
import com.example.domain.Piece
import com.example.domain.PieceType
import com.example.navigation.ScreenState
import com.example.presentation.ChessViewModel
import com.example.presentation.components.ChessBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayOfflineScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.offlineGameState.collectAsState()
    val theme = viewModel.activeTheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pass & Play", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.Home) },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Menu")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.restartOfflineGame() },
                        modifier = Modifier.testTag("restart_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart Match")
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
            
            // ————— TOP CAPTURES BAR (Black pieces captured by White) —————
            CapturesRow(
                capturedList = gameState.capturedPiecesWhite, // list of Black pieces captured
                colorLabel = "Black's losses",
                modifier = Modifier.fillMaxWidth()
            )

            // ————— ACTIVE TURN AND STATUS INDICATOR —————
            TurnIndicator(
                activeColor = gameState.activeColor,
                isCheck = gameState.isCheck,
                status = gameState.status
            )

            // ————— MAIN CHESS BOARD —————
            ChessBoard(
                board = gameState.board,
                selectedSquare = viewModel.offlineSelectedSquare,
                legalDestinations = viewModel.offlineLegalDestinations,
                activeTheme = theme,
                isCheck = gameState.isCheck,
                activeColor = gameState.activeColor,
                onSquareClick = { pos -> viewModel.handleOfflineSquareTap(pos) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            // ————— BOTTOM CAPTURES BAR (White pieces captured by Black) —————
            CapturesRow(
                capturedList = gameState.capturedPiecesBlack, // list of White pieces captured
                colorLabel = "White's losses",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ————— SCROLLABLE MOVE HISTORY LOG —————
            MoveHistoryPanel(
                history = gameState.history,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            // ————— OVERLAY WHEN GAME STATE REACHES AN ENDSTATE —————
            if (gameState.status != GameStatus.ACTIVE) {
                GameEndDialog(
                    status = gameState.status,
                    winnerColor = gameState.activeColor.opponent(),
                    onPlayAgain = { viewModel.restartOfflineGame() }
                )
            }
        }
    }
}

@Composable
fun CapturesRow(
    capturedList: List<Piece>,
    colorLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = colorLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            if (capturedList.isEmpty()) {
                Text(
                    text = "None yet",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(capturedList) { piece ->
                        val symbol = when (piece.type) {
                            PieceType.KING -> "♚"
                            PieceType.QUEEN -> "♛"
                            PieceType.ROOK -> "♜"
                            PieceType.BISHOP -> "♝"
                            PieceType.KNIGHT -> "♞"
                            PieceType.PAWN -> "♟"
                        }
                        val color = if (piece.color == ChessColor.WHITE) Color.White else Color(0xFF1A1A1A)
                        val outline = if (piece.color == ChessColor.WHITE) Color.Black else Color.White
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(outline.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = symbol, color = color, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TurnIndicator(
    activeColor: ChessColor,
    isCheck: Boolean,
    status: GameStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == GameStatus.ACTIVE) {
            val colorLabel = if (activeColor == ChessColor.WHITE) "White to move" else "Black to move"
            val badgeColor = if (activeColor == ChessColor.WHITE) Color.White else Color(0xFF1E1E24)
            val textColor = if (activeColor == ChessColor.WHITE) Color.Black else Color.White

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeColor)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isCheck) Color.Red else Color(0xFF00FF7F))
                    )
                    Text(
                        text = if (isCheck) "KING IN CHECK - $colorLabel!" else colorLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE53935))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "GAME OVER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MoveHistoryPanel(
    history: List<com.example.domain.Move>,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                text = "MOVE HISTORY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No moves made. Make your first Move!",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            } else {
                // Group moves in twos (Turn 1: White, Black)
                val pairedMoves = mutableListOf<String>()
                for (i in history.indices step 2) {
                    val turnNum = (i / 2) + 1
                    val whiteMove = history[i].toAlgebraic()
                    val blackMove = if (i + 1 < history.size) history[i + 1].toAlgebraic() else ""
                    pairedMoves.add("$turnNum. $whiteMove  $blackMove")
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pairedMoves) { moveStr ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = moveStr,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameEndDialog(
    status: GameStatus,
    winnerColor: ChessColor,
    onPlayAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Force action */ },
        title = {
            Text(
                text = if (status == GameStatus.CHECKMATE) "🏆 Checkmate!" else "🤝 Draw!",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            val announcement = when (status) {
                GameStatus.CHECKMATE -> {
                    val victorious = if (winnerColor == ChessColor.WHITE) "White" else "Black"
                    "Spectacular victory for $victorious! The opponent king has been cornered and has no escape!"
                }
                GameStatus.STALEMATE -> "Tactical deadlock achieved! The game ends in a Stalemate because the active player has no legal moves but is not in check."
                else -> "The game ends peacefully in a draw. Standard agreement or insufficient material has run its course."
            }
            Text(
                text = announcement,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play Another Match")
            }
        }
    )
}
