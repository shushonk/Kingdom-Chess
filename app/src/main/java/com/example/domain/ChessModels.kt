package com.example.domain

enum class ChessColor {
    WHITE, BLACK;
    fun opponent(): ChessColor = if (this == WHITE) BLACK else WHITE
}

enum class PieceType(val pieceVal: Int, val char: String) {
    PAWN(1, "P"),
    KNIGHT(3, "N"),
    BISHOP(3, "B"),
    ROOK(5, "R"),
    QUEEN(9, "Q"),
    KING(1000, "K")
}

data class Piece(
    val type: PieceType,
    val color: ChessColor,
    val id: Int = nextId()
) {
    companion object {
        private var currentId = 0
        @Synchronized
        fun nextId(): Int = ++currentId
    }
}

data class Position(val row: Int, val col: Int) {
    fun isValid(): Boolean = row in 0..7 && col in 0..7
    
    fun toAlgebraic(): String {
        val fileChar = ('a' + col)
        val rankNum = 8 - row
        return "$fileChar$rankNum"
    }

    companion object {
        fun fromAlgebraic(alg: String): Position? {
            if (alg.length != 2) return null
            val file = alg[0] - 'a'
            val rank = 8 - (alg[1] - '0')
            val pos = Position(rank, file)
            return if (pos.isValid()) pos else null
        }
    }
}

data class Move(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val isPromotion: Boolean = false,
    val promotionType: PieceType? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false,
    val castlingRookFrom: Position? = null,
    val castlingRookTo: Position? = null,
    val enPassantCapturedPos: Position? = null
) {
    fun toAlgebraic(): String {
        val suffix = if (isPromotion) "=${promotionType?.char ?: "Q"}" else ""
        return "${from.toAlgebraic()}${to.toAlgebraic()}$suffix"
    }
}

data class CastleRights(
    val whiteKingSide: Boolean = true,
    val whiteQueenSide: Boolean = true,
    val blackKingSide: Boolean = true,
    val blackQueenSide: Boolean = true
)
