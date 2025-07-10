package com.example.proyectochadfrontend.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.proyectochadfrontend.R

@Composable
fun AppScaffold(
    selectedItem: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedItem,
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick
            )
        },
        content = { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pantallabackground),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                content()
            }
        }
    )
}
