package com.example.proyectochadfrontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.Alignment
import com.example.proyectochadfrontend.ui.theme.*

@Composable
fun CyberpunkCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(CutCornerShape(12.dp))
            .background(cyberpunkDarkGray)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(cyberpunkCyan, cyberpunkPink)
                ),
                shape = CutCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}