package com.example.proyectochadfrontend.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.graphics.Color
import com.example.proyectochadfrontend.ui.theme.cyberpunkYellow
import com.example.proyectochadfrontend.ui.theme.cyberpunkCyan
import com.example.proyectochadfrontend.ui.theme.cyberpunkPink

@Composable
fun CyberpunkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = cyberpunkCyan) },
        singleLine = singleLine,
        modifier = modifier,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.Gray,
            cursorColor = cyberpunkPink,
            focusedIndicatorColor = cyberpunkCyan,
            unfocusedIndicatorColor = cyberpunkYellow,
            focusedLabelColor = cyberpunkCyan,
            unfocusedLabelColor = cyberpunkYellow,
        )
    )
}