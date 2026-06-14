package com.example.domain

data class ChessLesson(
    val id: Int,
    val category: String,
    val title: String,
    val description: String,
    val details: List<LessonSection>
)

data class LessonSection(
    val title: String,
    val text: String
)

object LearnRepository {
    val lessons = listOf(
        ChessLesson(
            id = 1,
            category = "Fundamentals",
            title = "How Pieces Move",
            description = "Master the movement patterns of all six chess pieces.",
            details = listOf(
                LessonSection(
                    title = "The Pawn (♙)",
                    text = "Pawns move forward 1 square at a time (or 2 squares on their first move). They capture diagonally forward 1 square. They are the only pieces that cannot move backwards."
                ),
                LessonSection(
                    title = "The Knight (♘)",
                    text = "Knights move in an 'L' shape: 2 squares in one direction and 1 square perpendicular. They are the only pieces that can jump over other pieces!"
                ),
                LessonSection(
                    title = "The Bishop (♗)",
                    text = "Bishops move diagonally as many squares as they want. They must stay on their starting square color (Light-squared or Dark-squared) for the entire game."
                ),
                LessonSection(
                    title = "The Rook (♖)",
                    text = "Rooks move horizontally or vertically as many squares as they want. They are powerful heavy pieces, especially on open lines."
                ),
                LessonSection(
                    title = "The Queen (♕)",
                    text = "The Queen is the most powerful piece in chess. She moves in any direction (horizontal, vertical, or diagonal) as many squares as she wants."
                ),
                LessonSection(
                    title = "The King (♔)",
                    text = "The King is the most important piece. He moves exactly 1 square in any direction. Protect your King at all costs—if he is trapped, the game is lost!"
                )
            )
        ),
        ChessLesson(
            id = 2,
            category = "Core Concepts",
            title = "Check and Checkmate",
            description = "Learn how to attack the opponent's king and finish the game.",
            details = listOf(
                LessonSection(
                    title = "What is a Check?",
                    text = "A King is in 'Check' when it is under direct threat of capture by an opponent's piece. The checked player must immediately escape check by moving the King, blocking the check, or capturing the attacking piece."
                ),
                LessonSection(
                    title = "What is Checkmate?",
                    text = "Checkmate occurs when a King is in check and has absolutely no legal moves to escape. This immediately ends the game, resulting in a victory for the attacking player."
                ),
                LessonSection(
                    title = "What is Stalemate?",
                    text = "Stalemate occurs when the player whose turn it is has NO legal moves, but their King is NOT in check. This is an immediate draw—it is a classic way for a losing player to save half a point!"
                )
            )
        ),
        ChessLesson(
            id = 3,
            category = "Special Moves",
            title = "Castling & En Passant",
            description = "Explore advanced maneuvers to secure your king and capture pawns.",
            details = listOf(
                LessonSection(
                    title = "Castling Requirements",
                    text = "Castling lets you move your King 2 squares sideways and jump the Rook over it. It is allowed ONLY if: \n1. Neither King nor Rook has moved yet.\n2. No pieces are between them.\n3. The King is NOT currently in check.\n4. The King does NOT pass through or land on a square under attack."
                ),
                LessonSection(
                    title = "En Passant (In Passing)",
                    text = "If an opponent pawn advances 2 squares forward from its starting position and lands adjacent to yours, you can capture it diagonally 'in passing', as if it had only moved 1 square. This capture is ONLY allowed on the very next move."
                )
            )
        ),
        ChessLesson(
            id = 4,
            category = "Special Moves",
            title = "Pawn Promotion",
            description = "Guide your humble pawn to the end of the board to unlock raw power.",
            details = listOf(
                LessonSection(
                    title = "Reaching the End",
                    text = "When a Pawn marches all the way to the 8th rank (opposite end of the board), it must immediately promote. The player can choose to promote it into a Queen, Rook, Bishop, or Knight. It cannot remain a Pawn or become a King."
                ),
                LessonSection(
                    title = "Promotion Strategy",
                    text = "Promoting to a Queen is usually the best choice due to its high mobility! However, promoting to a Knight (known as underpromoting) is sometimes chosen to deliver a surprising tactical check or royal fork."
                )
            )
        ),
        ChessLesson(
            id = 5,
            category = "Strategy",
            title = "Common Openings",
            description = "Start your game like a Grandmaster with structural control.",
            details = listOf(
                LessonSection(
                    title = "The Italian Game (1. e4 e5 2. Nf3 Nc6 3. Bc4)",
                    text = "One of the oldest chess openings. It focuses on rapid development, fighting for control of the center (squares d4/e4), and immediately targeting Black's weak f7 pawn with the light-squared bishop."
                ),
                LessonSection(
                    title = "The Queen's Gambit (1. d4 d5 2. c4)",
                    text = "White sacrifices a side pawn (the c4 pawn) to lure Black's d5 pawn away from the center. White plans to establish absolute control of the center soon using e2-e4."
                ),
                LessonSection(
                    title = "Golden Rules of Openings",
                    text = "1. Control the center (d4, d5, e4, e5).\n2. Develop Knights and Bishops early.\n3. Castle early to secure your King.\n4. Do not move the same piece multiple times in the opening."
                )
            )
        )
    )
}
