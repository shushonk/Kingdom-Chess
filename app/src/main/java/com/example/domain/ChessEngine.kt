package com.example.domain

import kotlin.math.abs

object ChessEngine {

    /**
     * Finds the Position of the King for a given color.
     */
    fun findKing(board: Array<Array<Piece?>>, color: ChessColor): Position? {
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.type == PieceType.KING && piece.color == color) {
                    return Position(r, c)
                }
            }
        }
        return null
    }

    /**
     * Checks if the King of the specified color is currently under attack.
     */
    fun isInCheck(board: Array<Array<Piece?>>, color: ChessColor, state: ChessGameState): Boolean {
        val kingPos = findKing(board, color) ?: return false
        
        // Loop through all opponent pieces and check if any can attack kingPos
        val opponentColor = color.opponent()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.color == opponentColor) {
                    val attacks = getPseudolegalMoves(board, Position(r, c), state, ignoreCastling = true)
                    for (attack in attacks) {
                        if (attack.to == kingPos) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     * Generates pseudolegal moves (ignoring whether they expose own King to check).
     */
    fun getPseudolegalMoves(
        board: Array<Array<Piece?>>,
        from: Position,
        state: ChessGameState,
        ignoreCastling: Boolean = false
    ): List<Move> {
        val piece = board[from.row][from.col] ?: return emptyList()
        val moves = mutableListOf<Move>()
        val color = piece.color

        when (piece.type) {
            PieceType.PAWN -> {
                val dir = if (color == ChessColor.WHITE) -1 else 1
                val startRow = if (color == ChessColor.WHITE) 6 else 1
                val promoRow = if (color == ChessColor.WHITE) 0 else 7

                // 1. Move forward 1
                val f1 = Position(from.row + dir, from.col)
                if (f1.isValid() && board[f1.row][f1.col] == null) {
                    if (f1.row == promoRow) {
                        // Promotion moves
                        arrayOf(PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP).forEach { type ->
                            moves.add(Move(from, f1, piece, isPromotion = true, promotionType = type))
                        }
                    } else {
                        moves.add(Move(from, f1, piece))
                    }

                    // 2. Move forward 2 (only if starting row and 1st square was empty)
                    val f2 = Position(from.row + 2 * dir, from.col)
                    if (from.row == startRow && f2.isValid() && board[f2.row][f2.col] == null) {
                        moves.add(Move(from, f2, piece))
                    }
                }

                // 3. Diagonal captures
                val capCols = intArrayOf(-1, 1)
                for (dc in capCols) {
                    val to = Position(from.row + dir, from.col + dc)
                    if (to.isValid()) {
                        val tar = board[to.row][to.col]
                        if (tar != null && tar.color != color) {
                            if (to.row == promoRow) {
                                arrayOf(PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP).forEach { type ->
                                    moves.add(Move(from, to, piece, capturedPiece = tar, isPromotion = true, promotionType = type))
                                }
                            } else {
                                moves.add(Move(from, to, piece, capturedPiece = tar))
                            }
                        }

                        // En Passant
                        if (state.enPassantTarget == to) {
                            val epCapturedRow = from.row
                            val epCapturedCol = to.col
                            val epCapturedPiece = board[epCapturedRow][epCapturedCol]
                            if (epCapturedPiece != null && epCapturedPiece.color != color && epCapturedPiece.type == PieceType.PAWN) {
                                moves.add(
                                    Move(
                                        from, to, piece,
                                        capturedPiece = epCapturedPiece,
                                        isEnPassant = true,
                                        enPassantCapturedPos = Position(epCapturedRow, epCapturedCol)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            PieceType.KNIGHT -> {
                val offsets = arrayOf(
                    Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
                    Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
                )
                for (o in offsets) {
                    val to = Position(from.row + o.first, from.col + o.second)
                    if (to.isValid()) {
                        val target = board[to.row][to.col]
                        if (target == null) {
                            moves.add(Move(from, to, piece))
                        } else if (target.color != color) {
                            moves.add(Move(from, to, piece, capturedPiece = target))
                        }
                    }
                }
            }

            PieceType.BISHOP -> {
                val dirs = arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
                for (d in dirs) {
                    var r = from.row + d.first
                    var c = from.col + d.second
                    while (r in 0..7 && c in 0..7) {
                        val to = Position(r, c)
                        val target = board[r][c]
                        if (target == null) {
                            moves.add(Move(from, to, piece))
                        } else {
                            if (target.color != color) {
                                moves.add(Move(from, to, piece, capturedPiece = target))
                            }
                            break // Blocked
                        }
                        r += d.first
                        c += d.second
                    }
                }
            }

            PieceType.ROOK -> {
                val dirs = arrayOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
                for (d in dirs) {
                    var r = from.row + d.first
                    var c = from.col + d.second
                    while (r in 0..7 && c in 0..7) {
                        val to = Position(r, c)
                        val target = board[r][c]
                        if (target == null) {
                            moves.add(Move(from, to, piece))
                        } else {
                            if (target.color != color) {
                                moves.add(Move(from, to, piece, capturedPiece = target))
                            }
                            break // Blocked
                        }
                        r += d.first
                        c += d.second
                    }
                }
            }

            PieceType.QUEEN -> {
                val dirs = arrayOf(
                    Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1),
                    Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)
                )
                for (d in dirs) {
                    var r = from.row + d.first
                    var c = from.col + d.second
                    while (r in 0..7 && c in 0..7) {
                        val to = Position(r, c)
                        val target = board[r][c]
                        if (target == null) {
                            moves.add(Move(from, to, piece))
                        } else {
                            if (target.color != color) {
                                moves.add(Move(from, to, piece, capturedPiece = target))
                            }
                            break // Blocked
                        }
                        r += d.first
                        c += d.second
                    }
                }
            }

            PieceType.KING -> {
                val dirs = arrayOf(
                    Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1),
                    Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)
                )
                for (d in dirs) {
                    val to = Position(from.row + d.first, from.col + d.second)
                    if (to.isValid()) {
                        val target = board[to.row][to.col]
                        if (target == null) {
                            moves.add(Move(from, to, piece))
                        } else if (target.color != color) {
                            moves.add(Move(from, to, piece, capturedPiece = target))
                        }
                    }
                }

                // Castling Logic
                if (!ignoreCastling && !state.isCheck) {
                    val row = if (color == ChessColor.WHITE) 7 else 0
                    
                    // King Side Castle
                    val canKingCastle = if (color == ChessColor.WHITE) state.castleRights.whiteKingSide else state.castleRights.blackKingSide
                    if (canKingCastle && from.row == row && from.col == 4) {
                        val rookPos = Position(row, 7)
                        val rPiece = board[row][7]
                        if (rPiece != null && rPiece.type == PieceType.ROOK && rPiece.color == color) {
                            // Check empty spaces
                            if (board[row][5] == null && board[row][6] == null) {
                                // Squares must not be under attack
                                var pathClean = true
                                for (colAttack in arrayOf(5, 6)) {
                                    val testBoard = Array(8) { r -> board[r].clone() }
                                    // Temp move king
                                    testBoard[row][colAttack] = piece
                                    testBoard[row][from.col] = null
                                    if (isInCheck(testBoard, color, state)) {
                                        pathClean = false
                                        break
                                    }
                                }
                                if (pathClean) {
                                    moves.add(
                                        Move(
                                            from = from,
                                            to = Position(row, 6),
                                            piece = piece,
                                            isCastling = true,
                                            castlingRookFrom = rookPos,
                                            castlingRookTo = Position(row, 5)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Queen Side Castle
                    val canQueenCastle = if (color == ChessColor.WHITE) state.castleRights.whiteQueenSide else state.castleRights.blackQueenSide
                    if (canQueenCastle && from.row == row && from.col == 4) {
                        val rookPos = Position(row, 0)
                        val rPiece = board[row][0]
                        if (rPiece != null && rPiece.type == PieceType.ROOK && rPiece.color == color) {
                            // Check empty spaces
                            if (board[row][1] == null && board[row][2] == null && board[row][3] == null) {
                                var pathClean = true
                                // Squares king passes through/lands on: 2, 3 must not be under attack
                                for (colAttack in arrayOf(2, 3)) {
                                    val testBoard = Array(8) { r -> board[r].clone() }
                                    // Temp move king
                                    testBoard[row][colAttack] = piece
                                    testBoard[row][from.col] = null
                                    if (isInCheck(testBoard, color, state)) {
                                        pathClean = false
                                        break
                                    }
                                }
                                if (pathClean) {
                                    moves.add(
                                        Move(
                                            from = from,
                                            to = Position(row, 2),
                                            piece = piece,
                                            isCastling = true,
                                            castlingRookFrom = rookPos,
                                            castlingRookTo = Position(row, 3)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return moves
    }

    /**
     * Filters pseudolegal moves to only those that do not leave the king in check.
     */
    fun getLegalMoves(board: Array<Array<Piece?>>, from: Position, state: ChessGameState): List<Move> {
        val pseudolegal = getPseudolegalMoves(board, from, state)
        val legalMoves = mutableListOf<Move>()
        val color = board[from.row][from.col]?.color ?: return emptyList()

        for (m in pseudolegal) {
            // Apply move on clone board
            val cloneBoard = Array(8) { r -> board[r].clone() }
            
            // Standard move execution
            cloneBoard[m.to.row][m.to.col] = cloneBoard[m.from.row][m.from.col]
            cloneBoard[m.from.row][m.from.col] = null

            // En passant extra clear
            if (m.isEnPassant && m.enPassantCapturedPos != null) {
                cloneBoard[m.enPassantCapturedPos.row][m.enPassantCapturedPos.col] = null
            }

            // Castling extra rook move
            if (m.isCastling && m.castlingRookFrom != null && m.castlingRookTo != null) {
                cloneBoard[m.castlingRookTo.row][m.castlingRookTo.col] = cloneBoard[m.castlingRookFrom.row][m.castlingRookFrom.col]
                cloneBoard[m.castlingRookFrom.row][m.castlingRookFrom.col] = null
            }

            // Check if king is in check after move
            if (!isInCheck(cloneBoard, color, state)) {
                legalMoves.add(m)
            }
        }
        return legalMoves
    }

    /**
     * Gets all legal moves for the active color.
     */
    fun getAllLegalMoves(state: ChessGameState): List<Move> {
        val moves = mutableListOf<Move>()
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = state.board[r][c]
                if (piece != null && piece.color == state.activeColor) {
                    moves.addAll(getLegalMoves(state.board, Position(r, c), state))
                }
            }
        }
        return moves
    }

    /**
     * Executes a legal move on the gamestate, returning the next updated state.
     */
    fun makeMove(state: ChessGameState, move: Move): ChessGameState {
        val nextBoard = state.copyBoard()
        val movingPiece = nextBoard[move.from.row][move.from.col] ?: move.piece
        
        // Setup details for promotion
        val pieceToPut = if (move.isPromotion && move.promotionType != null) {
            Piece(move.promotionType, movingPiece.color)
        } else {
            movingPiece
        }

        // Apply primary Move
        nextBoard[move.to.row][move.to.col] = pieceToPut
        nextBoard[move.from.row][move.from.col] = null

        // Pass-through structures (captures)
        var capturedPiece = move.capturedPiece
        val capWhite = state.capturedPiecesWhite.toMutableList()
        val capBlack = state.capturedPiecesBlack.toMutableList()

        if (move.isEnPassant && move.enPassantCapturedPos != null) {
            capturedPiece = nextBoard[move.enPassantCapturedPos.row][move.enPassantCapturedPos.col] ?: move.capturedPiece
            nextBoard[move.enPassantCapturedPos.row][move.enPassantCapturedPos.col] = null
        }

        // Castling supplementary details
        if (move.isCastling && move.castlingRookFrom != null && move.castlingRookTo != null) {
            val rook = nextBoard[move.castlingRookFrom.row][move.castlingRookFrom.col]
            nextBoard[move.castlingRookTo.row][move.castlingRookTo.col] = rook
            nextBoard[move.castlingRookFrom.row][move.castlingRookFrom.col] = null
        }

        // Save captures
        if (capturedPiece != null) {
            if (capturedPiece.color == ChessColor.WHITE) {
                capBlack.add(capturedPiece) // Black captured White
            } else {
                capWhite.add(capturedPiece) // White captured Black
            }
        }

        // Update Castle Rights
        var whiteKingSide = state.castleRights.whiteKingSide
        var whiteQueenSide = state.castleRights.whiteQueenSide
        var blackKingSide = state.castleRights.blackKingSide
        var blackQueenSide = state.castleRights.blackQueenSide

        // King moves
        if (movingPiece.type == PieceType.KING) {
            if (movingPiece.color == ChessColor.WHITE) {
                whiteKingSide = false
                whiteQueenSide = false
            } else {
                blackKingSide = false
                blackQueenSide = false
            }
        }

        // Rook moves or captured/removed
        if (move.from == Position(7, 0) || move.to == Position(7, 0)) whiteQueenSide = false
        if (move.from == Position(7, 7) || move.to == Position(7, 7)) whiteKingSide = false
        if (move.from == Position(0, 0) || move.to == Position(0, 0)) blackQueenSide = false
        if (move.from == Position(0, 7) || move.to == Position(0, 7)) blackKingSide = false

        val nextCastleRights = CastleRights(whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide)

        // Update En Passant Target
        val nextEnPassantTarget = if (movingPiece.type == PieceType.PAWN && abs(move.to.row - move.from.row) == 2) {
            // Target is the skipped square
            val midRow = (move.from.row + move.to.row) / 2
            Position(midRow, move.from.col)
        } else {
            null
        }

        val nextActiveColor = state.activeColor.opponent()

        // Temporarily form next state to verify check and mate
        val tempState = ChessGameState(
            board = nextBoard,
            activeColor = nextActiveColor,
            history = state.history + move,
            capturedPiecesWhite = capWhite,
            capturedPiecesBlack = capBlack,
            castleRights = nextCastleRights,
            enPassantTarget = nextEnPassantTarget
        )

        // Decide checks and available next legal moves
        val nextIsCheck = isInCheck(nextBoard, nextActiveColor, tempState)
        val opponentLegalMoves = getAllLegalMoves(tempState)

        val nextStatus = when {
            opponentLegalMoves.isEmpty() -> {
                if (nextIsCheck) GameStatus.CHECKMATE else GameStatus.STALEMATE
            }
            // Insufficient material check can be added or standard 50-move but let's keep it simple
            else -> GameStatus.ACTIVE
        }

        return tempState.copy(
            isCheck = nextIsCheck,
            status = nextStatus
        )
    }
}
