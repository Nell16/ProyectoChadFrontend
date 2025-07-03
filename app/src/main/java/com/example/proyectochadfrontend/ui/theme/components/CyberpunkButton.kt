package com.example.proyectochadfrontend.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.graphics.Color
import com.example.proyectochadfrontend.ui.theme.cyberpunkYellow
import com.example.proyectochadfrontend.ui.theme.cyberpunkCyan

@Composable
fun CyberpunkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = modifier
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(cyberpunkYellow, cyberpunkCyan)
                ),
                shape = CutCornerShape(8.dp)
            )
            .padding(2.dp),
        enabled = enabled,
        shape = CutCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = cyberpunkYellow,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}