package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigation.ScreenState
import com.example.presentation.ChessViewModel

data class HomeMenuItem(
    val title: String,
    val subtitle: String,
    val iconString: String,
    val colorAccent: Color,
    val targetScreen: ScreenState,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        HomeMenuItem(
            title = "Play Offline",
            subtitle = "Local Pass & Play",
            iconString = "👥",
            colorAccent = Color(0xFF64B5F6),
            targetScreen = ScreenState.PlayOffline,
            testTag = "play_offline_button"
        ),
        HomeMenuItem(
            title = "Vs Computer",
            subtitle = "Adaptive Engine AI",
            iconString = "🤖",
            colorAccent = Color(0xFFFF8A65),
            targetScreen = ScreenState.PlayVsComputer,
            testTag = "play_computer_button"
        ),
        HomeMenuItem(
            title = "Tactical Puzzles",
            subtitle = "Sharpen Chess Sight",
            iconString = "⚡",
            colorAccent = Color(0xFFFFD54F),
            targetScreen = ScreenState.Puzzles,
            testTag = "puzzles_button"
        ),
        HomeMenuItem(
            title = "Learn Chess",
            subtitle = "Interactive Lessons",
            iconString = "🎓",
            colorAccent = Color(0xFF81C784),
            targetScreen = ScreenState.Learn,
            testTag = "learn_button"
        ),
        HomeMenuItem(
            title = "Analysis Board",
            subtitle = "Sandbox Position Lab",
            iconString = "🔍",
            colorAccent = Color(0xFFBA68C8),
            targetScreen = ScreenState.Analysis,
            testTag = "analysis_button"
        ),
        HomeMenuItem(
            title = "Kingdom Roadmap",
            subtitle = "1000+ Feature Plans",
            iconString = "📜",
            colorAccent = Color(0xFF81D4FA),
            targetScreen = ScreenState.Roadmap,
            testTag = "roadmap_button"
        ),
        HomeMenuItem(
            title = "App Settings",
            subtitle = "Sensory & Board Setup",
            iconString = "⚙️",
            colorAccent = Color(0xFF90A4AE),
            targetScreen = ScreenState.Settings,
            testTag = "settings_button"
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "KINGDOM CHESS",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Billboard Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rule the Board",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rule the board, master the kingdom in native flat or immersive 3D graphics.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "🏰",
                        fontSize = 48.sp
                    )
                }
            }

            Text(
                text = "Select Royal Chamber",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            // Dynamic grid list of modules
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag(item.testTag)
                            .clickable { viewModel.navigateTo(item.targetScreen) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(item.colorAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.iconString, fontSize = 22.sp)
                                }
                            }

                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
