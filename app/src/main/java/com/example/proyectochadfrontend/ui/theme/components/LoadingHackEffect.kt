package com.example.proyectochadfrontend.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.ui.theme.*

@Composable
fun LoadingHackEffect(modifier: Modifier = Modifier) {
    val loadingDots = rememberInfiniteTransition().animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Restart
        )
    )

    val dots = ".".repeat(loadingDots.value)

    Text(
        text = "Conectando$dots",
        color = cyberpunkPink,
        fontSize = 18.sp,
        modifier = modifier
    )
}