package com.example.domain

enum class GameStatus {
    ACTIVE,
    CHECKMATE,
    STALEMATE,
    DRAW
}

data class ChessGameState(
    val board: Array<Array<Piece?>> = createInitialBoard(),
    val activeColor: ChessColor = ChessColor.WHITE,
    val history: List<Move> = emptyList(),
    val capturedPiecesWhite: List<Piece> = emptyList(), // Pieces captured by WHITE (i.e. Black pieces)
    val capturedPiecesBlack: List<Piece> = emptyList(), // Pieces captured by BLACK (i.e. White pieces)
    val castleRights: CastleRights = CastleRights(),
    val enPassantTarget: Position? = null,
    val isCheck: Boolean = false,
    val status: GameStatus = GameStatus.ACTIVE
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChessGameState) return false

        if (!board.contentDeepEquals(other.board)) return false
        if (activeColor != other.activeColor) return false
        if (history != other.history) return false
        if (capturedPiecesWhite != other.capturedPiecesWhite) return false
        if (capturedPiecesBlack != other.capturedPiecesBlack) return false
        if (castleRights != other.castleRights) return false
        if (enPassantTarget != other.enPassantTarget) return false
        if (isCheck != other.isCheck) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + activeColor.hashCode()
        result = 31 * result + history.hashCode()
        result = 31 * result + capturedPiecesWhite.hashCode()
        result = 31 * result + capturedPiecesBlack.hashCode()
        result = 31 * result + castleRights.hashCode()
        result = 31 * result + (enPassantTarget?.hashCode() ?: 0)
        result = 31 * result + isCheck.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }

    fun copyBoard(): Array<Array<Piece?>> {
        return Array(8) { row -> board[row].clone() }
    }
}

fun createInitialBoard(): Array<Array<Piece?>> {
    val board = Array(8) { arrayOfNulls<Piece>(8) }
    
    // Black primary pieces
    board[0][0] = Piece(PieceType.ROOK, ChessColor.BLACK)
    board[0][1] = Piece(PieceType.KNIGHT, ChessColor.BLACK)
    board[0][2] = Piece(PieceType.BISHOP, ChessColor.BLACK)
    board[0][3] = Piece(PieceType.QUEEN, ChessColor.BLACK)
    board[0][4] = Piece(PieceType.KING, ChessColor.BLACK)
    board[0][5] = Piece(PieceType.BISHOP, ChessColor.BLACK)
    board[0][6] = Piece(PieceType.KNIGHT, ChessColor.BLACK)
    board[0][7] = Piece(PieceType.ROOK, ChessColor.BLACK)
    
    // Black pawns
    for (col in 0..7) {
        board[1][col] = Piece(PieceType.PAWN, ChessColor.BLACK)
    }
    
    // White pawns
    for (col in 0..7) {
        board[6][col] = Piece(PieceType.PAWN, ChessColor.WHITE)
    }
    
    // White primary pieces
    board[7][0] = Piece(PieceType.ROOK, ChessColor.WHITE)
    board[7][1] = Piece(PieceType.KNIGHT, ChessColor.WHITE)
    board[7][2] = Piece(PieceType.BISHOP, ChessColor.WHITE)
    board[7][3] = Piece(PieceType.QUEEN, ChessColor.WHITE)
    board[7][4] = Piece(PieceType.KING, ChessColor.WHITE)
    board[7][5] = Piece(PieceType.BISHOP, ChessColor.WHITE)
    board[7][6] = Piece(PieceType.KNIGHT, ChessColor.WHITE)
    board[7][7] = Piece(PieceType.ROOK, ChessColor.WHITE)
    
    return board
}
