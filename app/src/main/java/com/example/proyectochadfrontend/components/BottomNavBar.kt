package com.example.proyectochadfrontend.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.proyectochadfrontend.ui.theme.*

@Composable
fun BottomNavBar(
    selectedItem: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF1C1C1C),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    tint = if (selectedItem == "home") cyberpunkYellow else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    "Inicio",
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (selectedItem == "home") cyberpunkYellow else Color.Gray
                )
            },
            selected = selectedItem == "home",
            onClick = onHomeClick
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = if (selectedItem == "perfil") cyberpunkPink else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    "Perfil",
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (selectedItem == "perfil") cyberpunkPink else Color.Gray
                )
            },
            selected = selectedItem == "perfil",
            onClick = onProfileClick
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = if (selectedItem == "configuracion") cyberpunkCyan else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    "Configuración.",
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (selectedItem == "configuracion") cyberpunkCyan else Color.Gray
                )
            },
            selected = selectedItem == "configuracion",
            onClick = onSettingsClick
        )
    }
}
