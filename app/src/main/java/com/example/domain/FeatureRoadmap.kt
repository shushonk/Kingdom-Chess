package com.example.domain

data class RoadmapFeature(
    val title: String,
    val description: String,
    val category: String,
    val status: CodeStatus
)

enum class CodeStatus {
    IMPLEMENTED,
    COMING_SOON,
    PLANNED
}

object FeatureRoadmap {
    val tagline = "Rule the board. Master the kingdom."
    val developerCredit = "Created by Shashank V"
    val projectGoal = "1000+ Feature-Ready Roadmap for the ultimate royal chess experience."

    val categories = listOf(
        "Gameplay Features",
        "AI Features",
        "Learning Features",
        "UI Features",
        "Online Future Features",
        "Analysis Features",
        "Profile Features",
        "Accessibility Features",
        "Performance Features",
        "Monetization Future"
    )

    val features = listOf(
        // Gameplay Features
        RoadmapFeature("Play Offline Mode", "Local pass-and-play matches with a friend.", "Gameplay Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Vs Computer Mode", "Challenge the local chess engine.", "Gameplay Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("3D Chess Arena", "Play chess on a realistic 3D royal board with tilt and camera controls.", "Gameplay Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Tactical Puzzles", "Sharpen your chess sight by solving master puzzles.", "Gameplay Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Blitz & Rapid Mode", "Configurable clocks for high-speed tournaments.", "Gameplay Features", CodeStatus.COMING_SOON),
        RoadmapFeature("Bullet Chess", "1-minute hyper-fast reflex chess.", "Gameplay Features", CodeStatus.PLANNED),
        RoadmapFeature("Chess960 (Fischer Random)", "Randomized starting positions to eliminate memorize-by-heart openings.", "Gameplay Features", CodeStatus.PLANNED),
        RoadmapFeature("King of the Hill", "Race your king to one of the four center squares to win.", "Gameplay Features", CodeStatus.PLANNED),
        RoadmapFeature("Three-Check Chess", "Check the opposing king three times to claim victory.", "Gameplay Features", CodeStatus.PLANNED),
        RoadmapFeature("Atomic Chess", "Explosive captures that blast away adjacent pieces.", "Gameplay Features", CodeStatus.PLANNED),
        RoadmapFeature("Horde & Handicap Modes", "Asymmetric armies where pawns battle a regular set.", "Gameplay Features", CodeStatus.PLANNED),
        RoadmapFeature("Puzzle Rush", "Solve as many puzzles as possible under a 3-minute limit.", "Gameplay Features", CodeStatus.PLANNED),

        // AI Features
        RoadmapFeature("Easy AI Tier", "Perfect for beginners learning to move pieces.", "AI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Medium AI Tier", "Intermediary play with smart positional awareness.", "AI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Hard AI Tier", "Challenging Minimax AI with deep alpha-beta pruning lookahead.", "AI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Stockfish WASM/Native Integration", "Full integration of the Stockfish engine.", "AI Features", CodeStatus.COMING_SOON),
        RoadmapFeature("AI Coach Insights", "Post-move explanation, telling you why a move was good or bad.", "AI Features", CodeStatus.PLANNED),
        RoadmapFeature("Real-time Blunder Detection", "Instantly alert players when a catastrophic blunder is made.", "AI Features", CodeStatus.PLANNED),
        RoadmapFeature("AI Puzzle Generator", "Generates custom puzzles from your own played games.", "AI Features", CodeStatus.PLANNED),

        // Learning Features
        RoadmapFeature("Interactive Lesson Modules", "Expandable beginner-friendly guides.", "Learning Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Movement Fundamentals", "Visual guides showing how every piece steps.", "Learning Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Tactics Trainer: Pins", "Mastering the pin tactic to lock opponent pieces.", "Learning Features", CodeStatus.COMING_SOON),
        RoadmapFeature("Tactics Trainer: Forks", "Strikng two active targets at once with knights and pawns.", "Learning Features", CodeStatus.COMING_SOON),
        RoadmapFeature("Mating Patterns Masterclass", "Step-by-step guides for Anastasias and Smothered mates.", "Learning Features", CodeStatus.PLANNED),
        RoadmapFeature("Pawn Structure Principles", "Islands, isolated, and passed pawns made easy.", "Learning Features", CodeStatus.PLANNED),

        // UI Features
        RoadmapFeature("AMOLED Dark Mode", "Deep black aesthetics that protect player eyes.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Board Themes (2D)", "Beautiful palettes like Emerald, Ocean, and Midnight.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Board Themes (3D)", "Royal collections including Royal Marble, Midnight Kingdom, and Golden Court.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Tactical Glow Highlights", "Indicate selections, check status, and capture opportunities.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Haptic Vibration Pulse", "Feedback on taps, capturing, and check alerts.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Volumetric Shadows", "Drop shadows under 3D chess pieces.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Polished Slide Transitions", "300ms high-fidelity screen transitions.", "UI Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Custom Adaptive Royal Icon", "Launcher icon conforming to Material Design guidelines.", "UI Features", CodeStatus.IMPLEMENTED),

        // Online Future Features
        RoadmapFeature("Safe Player Authentication", "Account creation to sync Elo and rewards.", "Online Future Features", CodeStatus.PLANNED),
        RoadmapFeature("Matchmaking Lobby", "Play ranked or casual chess against players globally.", "Online Future Features", CodeStatus.PLANNED),
        RoadmapFeature("Private Room Codes", "Challenge physical friends by sending simple codes.", "Online Future Features", CodeStatus.PLANNED),
        RoadmapFeature("Real-time Chat & Emotes", "Fun emotes and chat during intensive turn battles.", "Online Future Features", CodeStatus.PLANNED),
        RoadmapFeature("Global & Royal Leaderboards", "Compare local ranks with world grandmasters.", "Online Future Features", CodeStatus.PLANNED),

        // Analysis Features
        RoadmapFeature("Sandbox Free-play Lab", "Enforce coordinates or setup custom branches.", "Analysis Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Move History Notation", "Standard SAN notation tracked during plays.", "Analysis Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Position Editor & FEN Setup", "Input direct chess FEN configurations.", "Analysis Features", CodeStatus.COMING_SOON),
        RoadmapFeature("PGN Import & Export", "Save, share, or import external files for review.", "Analysis Features", CodeStatus.PLANNED),
        RoadmapFeature("Mistake & Blunder Review", "Color-coded move graph analyzing accurate paths.", "Analysis Features", CodeStatus.PLANNED),

        // Profile Features
        RoadmapFeature("Player Statistics Cabinet", "Detailed win/loss cards and metrics.", "Profile Features", CodeStatus.COMING_SOON),
        RoadmapFeature("Elo Rating Progress Charts", "Visual progress charts tracks ratings over puzzles.", "Profile Features", CodeStatus.PLANNED),
        RoadmapFeature("Achievements & Badges", "Unlock achievements for king hunts or castle plays.", "Profile Features", CodeStatus.PLANNED),

        // Accessibility Features
        RoadmapFeature("Large Text Support", "Full scaling support for system large fonts.", "Accessibility Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("High-Contrast Themes", "Specific visual palettes helping colorblind players.", "Accessibility Features", CodeStatus.COMING_SOON),
        RoadmapFeature("Voice Move Announcements", "Read moves aloud like Rook to e4.", "Accessibility Features", CodeStatus.PLANNED),

        // Performance Features
        RoadmapFeature("Low Performance Toggle", "Drops intensive Canvas paths to boost battery.", "Performance Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("FPS Counter Overlay", "Real-time frame metric tracking.", "Performance Features", CodeStatus.IMPLEMENTED),
        RoadmapFeature("Animation Quality Selector", "Smooth spring animations scale based on device hardware.", "Performance Features", CodeStatus.IMPLEMENTED),

        // Monetization
        RoadmapFeature("Support the Developer", "An optional screening page supporting developer's craft.", "Monetization Future", CodeStatus.PLANNED),
        RoadmapFeature("Cosmetic Themes Only", "Absolutely zero pay-to-win mechanics forever.", "Monetization Future", CodeStatus.PLANNED)
    )
}
