package com.example.domain

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

object ChessAI {

    /**
     * Chooses a move for the computer.
     */
    fun chooseMove(
        state: ChessGameState,
        difficulty: Difficulty
    ): Move? {
        val legalMoves = ChessEngine.getAllLegalMoves(state)
        if (legalMoves.isEmpty()) return null

        return when (difficulty) {
            Difficulty.EASY -> chooseEasyMove(legalMoves)
            Difficulty.MEDIUM -> chooseMediumMove(state, legalMoves)
            Difficulty.HARD -> chooseHardMove(state, legalMoves)
        }
    }

    private fun chooseEasyMove(legalMoves: List<Move>): Move {
        return legalMoves[Random.nextInt(legalMoves.size)]
    }

    private fun chooseMediumMove(state: ChessGameState, legalMoves: List<Move>): Move {
        // Evaluate immediately 1-ply captures
        var bestScore = -100000
        val candidates = mutableListOf<Move>()

        for (move in legalMoves) {
            var score = 0
            
            // Prioritize captures
            val captured = move.capturedPiece
            if (captured != null) {
                // Score according to standard piece values
                score += captured.type.pieceVal * 10
            }

            // Pawn promotion is good
            if (move.isPromotion) {
                score += (move.promotionType?.pieceVal ?: 9) * 5
            }

            // Small heuristic: knights & bishops moving towards center
            val toC = move.to.col
            val toR = move.to.row
            val isCenter = (toC in 3..4 && toR in 3..4)
            if (isCenter && (move.piece.type == PieceType.KNIGHT || move.piece.type == PieceType.BISHOP)) {
                score += 2
            }

            // Inject tiny random value to break ties
            score += Random.nextInt(2)

            if (score > bestScore) {
                bestScore = score
                candidates.clear()
                candidates.add(move)
            } else if (score == bestScore) {
                candidates.add(move)
            }
        }

        return candidates[Random.nextInt(candidates.size)]
    }

    private fun chooseHardMove(state: ChessGameState, legalMoves: List<Move>): Move {
        // Mini-Max algorithm of depth 2 with alpha-beta pruning
        val depth = 2
        val maximizing = (state.activeColor == ChessColor.WHITE)
        var bestVal = if (maximizing) -1000000 else 1000000
        val candidates = mutableListOf<Move>()

        for (move in legalMoves) {
            // Simulate the move
            val nextState = ChessEngine.makeMove(state, move)
            val v = minimax(nextState, depth - 1, -10000000, 10000000, !maximizing)

            if (maximizing) {
                if (v > bestVal) {
                    bestVal = v
                    candidates.clear()
                    candidates.add(move)
                } else if (v == bestVal) {
                    candidates.add(move)
                }
            } else {
                if (v < bestVal) {
                    bestVal = v
                    candidates.clear()
                    candidates.add(move)
                } else if (v == bestVal) {
                    candidates.add(move)
                }
            }
        }

        return if (candidates.isNotEmpty()) candidates[Random.nextInt(candidates.size)] else legalMoves[0]
    }

    private fun minimax(
        state: ChessGameState,
        depth: Int,
        alpha: Int,
        beta: Int,
        maximizingPlayer: Boolean
    ): Int {
        if (depth == 0 || state.status != GameStatus.ACTIVE) {
            return evaluateBoard(state)
        }

        val legalMoves = ChessEngine.getAllLegalMoves(state)
        if (legalMoves.isEmpty()) {
            return evaluateBoard(state)
        }

        var localAlpha = alpha
        var localBeta = beta

        if (maximizingPlayer) {
            var maxEval = -10000000
            for (move in legalMoves) {
                val nextState = ChessEngine.makeMove(state, move)
                val eval = minimax(nextState, depth - 1, localAlpha, localBeta, false)
                maxEval = max(maxEval, eval)
                localAlpha = max(localAlpha, eval)
                if (localBeta <= localAlpha) break
            }
            return maxEval
        } else {
            var minEval = 10000000
            for (move in legalMoves) {
                val nextState = ChessEngine.makeMove(state, move)
                val eval = minimax(nextState, depth - 1, localAlpha, localBeta, true)
                minEval = min(minEval, eval)
                localBeta = min(localBeta, eval)
                if (localBeta <= localAlpha) break
            }
            return minEval
        }
    }

    private fun evaluateBoard(state: ChessGameState): Int {
        // Checkmate is absolute priority
        if (state.status == GameStatus.CHECKMATE) {
            // If white is active and mated, black wins (-infinity)
            // If black is active and mated, white wins (+infinity)
            return if (state.activeColor == ChessColor.WHITE) -1000000 else 1000000
        }
        if (state.status == GameStatus.STALEMATE || state.status == GameStatus.DRAW) {
            return 0
        }

        var score = 0
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = state.board[r][c] ?: continue
                val sign = if (piece.color == ChessColor.WHITE) 1 else -1
                
                // Base piece value
                var valPiece = piece.type.pieceVal * 100

                // Positional bonuses
                when (piece.type) {
                    PieceType.PAWN -> {
                        // Pawns advancing is good
                        valPiece += if (piece.color == ChessColor.WHITE) (6 - r) * 10 else (r - 1) * 10
                    }
                    PieceType.KNIGHT -> {
                        // Knights belong in the center
                        val centerDist = abs(3.5 - r) + abs(3.5 - c)
                        valPiece += ((7.0 - centerDist) * 8).toInt()
                    }
                    PieceType.BISHOP -> {
                        valPiece += 10 // Active diagonals
                    }
                    PieceType.ROOK -> {
                        // Rooks on open files or back ranks are nice
                        valPiece += 5
                    }
                    PieceType.QUEEN -> {
                        valPiece += 2
                    }
                    PieceType.KING -> {
                        // Keep King safe at the corners early, or center late-game
                        val centerDist = abs(3.5 - r) + abs(3.5 - c)
                        valPiece += (centerDist * 5).toInt()
                    }
                }

                score += sign * valPiece
            }
        }

        // Add small bias if checking opponent
        if (state.isCheck) {
            score += if (state.activeColor == ChessColor.BLACK) 50 else -50
        }

        return score
    }
}
