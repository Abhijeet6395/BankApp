package com.example.bankapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController, userType: String) {
    BottomAppBar(
        containerColor = Color(0xFF6200EA),
        contentColor = Color.White
    ) {
        // Home Button
        IconButton(
            onClick = {
                val destination = when (userType) {
                    "banker" -> "bankerDetails"
                    else -> "customerDetails"
                }
                navController.navigate(destination)
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }

        // Accounts Button
        IconButton(
            onClick = {
                navController.navigate("accounts/$userType")

            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
        }

        // Settings Button
        IconButton(
            onClick = {
                navController.navigate("settings/$userType")
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}
