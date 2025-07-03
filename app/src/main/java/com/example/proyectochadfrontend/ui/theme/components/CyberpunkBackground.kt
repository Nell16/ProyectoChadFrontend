package com.example.proyectochadfrontend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.ui.theme.cyberpunkDarkGray
import com.example.proyectochadfrontend.ui.theme.cyberpunkCyan

@Composable
fun CyberpunkBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(cyberpunkDarkGray) //
    ) {
        // 🎞️ Efecto scanlines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineSpacing = 6.dp.toPx()
            val lineHeight = 1.dp.toPx()
            val height = size.height

            var y = 0f
            while (y < height) {
                drawLine(
                    color = cyberpunkCyan.copy(alpha = 0.06f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = lineHeight
                )
                y += lineSpacing
            }
        }
    }
}