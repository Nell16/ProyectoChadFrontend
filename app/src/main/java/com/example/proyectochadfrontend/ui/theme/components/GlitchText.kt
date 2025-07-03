package com.example.proyectochadfrontend.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.unit.TextUnit
import com.example.proyectochadfrontend.ui.theme.*

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    color: Color = cyberpunkCyan,
    style: TextStyle = TextStyle.Default.copy(fontSize = 28.sp)
) {
    val glitchOffset = rememberInfiniteTransition().animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Text(
            text = text,
            style = style,
            color = color,
            modifier = Modifier.drawWithContent {
                drawContent()
                drawContext.canvas.save()
                drawContext.canvas.translate(glitchOffset.value, glitchOffset.value)
                drawContent()
                drawContext.canvas.restore()
            }
        )
    }
}