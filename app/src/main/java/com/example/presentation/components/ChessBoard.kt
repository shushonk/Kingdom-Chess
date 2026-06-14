package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ChessColor
import com.example.domain.Piece
import com.example.domain.PieceType
import com.example.domain.Position
import com.example.presentation.BoardTheme

@Composable
fun ChessPiece(
    piece: Piece,
    size: Dp
) {
    val symbol = when (piece.type) {
        PieceType.KING -> "♚"
        PieceType.QUEEN -> "♛"
        PieceType.ROOK -> "♜"
        PieceType.BISHOP -> "♝"
        PieceType.KNIGHT -> "♞"
        PieceType.PAWN -> "♟"
    }

    // Pearls for White, charcoal onyx for Black
    val mainColor = if (piece.color == ChessColor.WHITE) {
        Color(0xFFFBFBFC)
    } else {
        Color(0xFF1E1E24)
    }

    // High contrast volumetric shadow outline
    val outlineShadowColor = if (piece.color == ChessColor.WHITE) {
        Color(0xFF4A4A4A)
    } else {
        Color(0xFFE2E8F0)
    }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = (size.value * 0.78f).sp,
            color = mainColor,
            style = TextStyle(
                shadow = Shadow(
                    color = outlineShadowColor,
                    offset = Offset(0f, 0f),
                    blurRadius = 6f
                )
            )
        )
    }
}

@Composable
fun ChessBoard(
    board: Array<Array<Piece?>>,
    selectedSquare: Position?,
    legalDestinations: List<Position>,
    activeTheme: BoardTheme,
    isCheck: Boolean,
    activeColor: ChessColor,
    onSquareClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val lightSquareColor = Color(android.graphics.Color.parseColor(activeTheme.lightSquareHex))
    val darkSquareColor = Color(android.graphics.Color.parseColor(activeTheme.darkSquareHex))
    val boardBorderColor = Color(android.graphics.Color.parseColor(activeTheme.borderHex))

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(4.dp, boardBorderColor, RoundedCornerShape(12.dp))
            .background(boardBorderColor)
    ) {
        val cellSize = maxWidth / 8

        Column {
            for (row in 0..7) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..7) {
                        val currentPos = Position(row, col)
                        val piece = board[row][col]

                        val isLight = (row + col) % 2 == 0
                        val baseColor = if (isLight) lightSquareColor else darkSquareColor

                        // Check status highlight
                        val isKingInCheck = isCheck && piece != null && piece.type == PieceType.KING && piece.color == activeColor

                        val isSelected = currentPos == selectedSquare
                        val isDestination = currentPos in legalDestinations

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(
                                    when {
                                        isKingInCheck -> Color(0xFFEF5350) // Crimson alert
                                        isSelected -> Color(0x90FFF59D)    // Golden glow
                                        else -> baseColor
                                    }
                                )
                                .clickable { onSquareClick(currentPos) },
                            contentAlignment = Alignment.Center
                        ) {
                            // Render coordinates along borders
                            if (col == 0) {
                                Text(
                                    text = (8 - row).toString(),
                                    color = if (isLight) darkSquareColor else lightSquareColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 2.dp, top = 1.dp)
                                )
                            }
                            if (row == 7) {
                                Text(
                                    text = ('a' + col).toString(),
                                    color = if (isLight) darkSquareColor else lightSquareColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 3.dp, bottom = 1.dp)
                                )
                            }

                            // Render piece
                            if (piece != null) {
                                ChessPiece(piece = piece, size = cellSize * 0.9f)
                            }

                            // Render move highlights
                            if (isDestination) {
                                if (piece == null) {
                                    // Empty square: tiny quiet dot
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize * 0.3f)
                                            .clip(CircleShape)
                                            .background(Color(0x5500E676))
                                            .border(1.5.dp, Color(0xFF00E676), CircleShape)
                                    )
                                } else {
                                    // Occupied square: Capture frame
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize * 0.95f)
                                            .clip(CircleShape)
                                            .border(4.dp, Color(0xAAFF5252), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
