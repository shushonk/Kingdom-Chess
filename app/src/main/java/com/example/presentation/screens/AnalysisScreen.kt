package com.example.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigation.ScreenState
import com.example.presentation.ChessViewModel
import com.example.presentation.components.ChessBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.analysisGameState.collectAsState()
    val theme = viewModel.activeTheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sandbox Lab", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenState.Home) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.restartAnalysisGame() },
                        modifier = Modifier.testTag("reset_analysis")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Board")
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
            
            // Helpful introductory guide text
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "SANDBOX LAB",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Move pieces freely with correct turn enforcement. Setup coordinates, evaluate openings, or test tactical branches.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Status message
            TurnIndicator(
                activeColor = gameState.activeColor,
                isCheck = gameState.isCheck,
                status = gameState.status
            )

            // ————— CHESS BOARD —————
            ChessBoard(
                board = gameState.board,
                selectedSquare = viewModel.analysisSelectedSquare,
                legalDestinations = viewModel.analysisLegalDestinations,
                activeTheme = theme,
                isCheck = gameState.isCheck,
                activeColor = gameState.activeColor,
                onSquareClick = { pos -> viewModel.handleAnalysisSquareTap(pos) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            // Move History
            MoveHistoryPanel(
                history = gameState.history,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
            )

            // Bottom controls
            Button(
                onClick = { viewModel.restartAnalysisGame() },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear and Reset Board", fontSize = 13.sp)
            }
        }
    }
}
