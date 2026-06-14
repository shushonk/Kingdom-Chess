package com.example.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {
    val scaleAnim = remember { Animatable(0.7f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F12),
                        Color(0xFF1B1B22)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnim.value)
                .padding(24.dp)
        ) {
            // Elegant large physical piece vector rendering
            Text(
                text = "♞",
                fontSize = 110.sp,
                color = Color(0xFFECECD7),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xFF739552),
                        offset = Offset(0f, 4f),
                        blurRadius = 16f
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Kingdom Chess",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xFFD4AF37),
                        offset = Offset(0f, 2f),
                        blurRadius = 12f
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "RULE THE BOARD. MASTER THE KINGDOM.",
                fontSize = 11.sp,
                letterSpacing = 2.5.sp,
                color = Color(0xFFAFAFAF),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(64.dp))

            CircularProgressIndicator(
                color = Color(0xFFD4AF37),
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Created by Shashank V",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0x80FFFFFF),
                letterSpacing = 1.sp
            )
        }
    }
}
