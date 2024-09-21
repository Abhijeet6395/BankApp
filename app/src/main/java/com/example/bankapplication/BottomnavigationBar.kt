package com.example.bankapplication

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
@Composable
fun BottomNavigationBar(navController: NavController, userType: String, bankViewModel: BankViewModel) {
    val email by bankViewModel.currentCustomerEmail.collectAsState()

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    BottomAppBar(
        containerColor = Color(0xFF6200EA),
        contentColor = Color.White
    ) {
        // Home Button
        IconButton(
            onClick = {
                val destination = when (userType) {
                    "banker" -> "bankerDetails/$email"
                    else -> "customerDetails/$email"
                }
                navController.navigate(destination) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (currentRoute?.startsWith("bankerDetails") == true || currentRoute?.startsWith("customerDetails") == true) {
                    Color.Yellow // Highlight selected icon
                } else {
                    Color.White
                }
            )
        }

        // Accounts Button
        IconButton(
            onClick = {
                navController.navigate("accounts/$userType") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Accounts",
                tint = if (currentRoute == "accounts/$userType") {
                    Color.Yellow // Highlight selected icon
                } else {
                    Color.White
                }
            )
        }

        // Settings Button
        IconButton(
            onClick = {
                navController.navigate("settings/$userType?email=$email") {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = if (currentRoute?.startsWith("settings") == true) {
                    Color.Yellow // Highlight selected icon
                } else {
                    Color.White
                }
            )
        }
    }
}
