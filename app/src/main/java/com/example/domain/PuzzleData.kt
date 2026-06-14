package com.example.domain

data class ChessPuzzle(
    val id: Int,
    val title: String,
    val description: String,
    val activeColor: ChessColor,
    val correctMoveFrom: Position,
    val correctMoveTo: Position,
    val resultMessage: String,
    val initialBoardSetup: () -> Array<Array<Piece?>>
)

object PuzzleRepository {
    val puzzles = listOf(
        ChessPuzzle(
            id = 1,
            title = "Back-Rank Checkmate",
            description = "Black's king is trapped on the back rank behind their own pawns. Find the winning Move in one move!",
            activeColor = ChessColor.WHITE,
            correctMoveFrom = Position(7, 2), // c1 Rook
            correctMoveTo = Position(0, 2), // c8 Rook (Checkmate)
            resultMessage = "Magnificent! You completed the classic Back-Rank mate! R-c8# is checkmate because the King has no breathing room.",
            initialBoardSetup = {
                val board = Array(8) { arrayOfNulls<Piece>(8) }
                // Black King trapped
                board[0][7] = Piece(PieceType.KING, ChessColor.BLACK)
                board[1][5] = Piece(PieceType.PAWN, ChessColor.BLACK)
                board[1][6] = Piece(PieceType.PAWN, ChessColor.BLACK)
                board[1][7] = Piece(PieceType.PAWN, ChessColor.BLACK)

                // White pieces
                board[7][7] = Piece(PieceType.KING, ChessColor.WHITE)
                board[7][2] = Piece(PieceType.ROOK, ChessColor.WHITE) // Rook on c1
                board[6][6] = Piece(PieceType.PAWN, ChessColor.WHITE)
                board[6][7] = Piece(PieceType.PAWN, ChessColor.WHITE)
                board
            }
        ),
        ChessPuzzle(
            id = 2,
            title = "The Golden Fork",
            description = "White has a royal fork opportunity. Find the move that attacks the Black King and Black Queen simultaneously!",
            activeColor = ChessColor.WHITE,
            correctMoveFrom = Position(4, 3), // d4 Knight
            correctMoveTo = Position(2, 2),   // c6 Knight (Fork)
            resultMessage = "Stunning find! Knight to c6 check forks Black's King (on a8) and Queen (on e7) winning the queen next move.",
            initialBoardSetup = {
                val board = Array(8) { arrayOfNulls<Piece>(8) }
                // Black King and Queen positions
                board[0][0] = Piece(PieceType.KING, ChessColor.BLACK) // a8 King
                board[1][4] = Piece(PieceType.QUEEN, ChessColor.BLACK) // e7 Queen
                
                // White Knight and King
                board[4][3] = Piece(PieceType.KNIGHT, ChessColor.WHITE) // d4 Knight
                board[7][7] = Piece(PieceType.KING, ChessColor.WHITE)   // h1 King
                board
            }
        ),
        ChessPuzzle(
            id = 3,
            title = "Smothered Mate",
            description = "Black is surrounding their own King. Find the spectacular Knight move that delivers an unavoidable checkmate!",
            activeColor = ChessColor.WHITE,
            correctMoveFrom = Position(2, 5), // f6 Knight
            correctMoveTo = Position(1, 6),   // g7 Knight (Mate)
            resultMessage = "Brilliant play! N-g7 is a smothered checkmate! The black King is trapped entirely by its own pieces (f8 Rook, g8/h7/h8 pawns).",
            initialBoardSetup = {
                val board = Array(8) { arrayOfNulls<Piece>(8) }
                // Black King surrounded
                board[0][7] = Piece(PieceType.KING, ChessColor.BLACK)  // h8 King
                board[0][5] = Piece(PieceType.ROOK, ChessColor.BLACK)  // f8 Rook
                board[1][7] = Piece(PieceType.PAWN, ChessColor.BLACK)  // h7 Pawn
                board[1][6] = Piece(PieceType.PAWN, ChessColor.BLACK)  // g7 Pawn
                
                // White Knight
                board[2][5] = Piece(PieceType.KNIGHT, ChessColor.WHITE) // f6 Knight
                board[7][0] = Piece(PieceType.KING, ChessColor.WHITE)   // a1 King
                board
            }
        ),
        ChessPuzzle(
            id = 4,
            title = "Tactical Skewer",
            description = "White can execute a lethal skewer. Attack the Black King along a diagonal to win the Black Rook sitting on the same diagonal!",
            activeColor = ChessColor.WHITE,
            correctMoveFrom = Position(3, 3), // d5 Bishop
            correctMoveTo = Position(1, 1),   // b7 Bishop (Skewer)
            resultMessage = "Fantastic tactical eye! Bishop to b7 check forces the black King to move away, revealing the unprotected black Rook on h1 for capture!",
            initialBoardSetup = {
                val board = Array(8) { arrayOfNulls<Piece>(8) }
                // Black King and Rook on same a8-h1 diagonal
                board[0][0] = Piece(PieceType.KING, ChessColor.BLACK) // a8 King
                board[7][7] = Piece(PieceType.ROOK, ChessColor.BLACK) // h1 Rook (unprotected)
                
                // White Bishop
                board[3][3] = Piece(PieceType.BISHOP, ChessColor.WHITE) // d5 Bishop
                board[6][0] = Piece(PieceType.KING, ChessColor.WHITE)   // a2 King
                board
            }
        )
    )
}
