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
fun BottomNavigationBar(navController: NavController, userType: String,bankViewModel: BankViewModel) {
    val email by bankViewModel.currentCustomerEmail.collectAsState()


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
                Log.d("Navigation", "Navigating to: $destination")

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
            Icon(Icons.Default.Home, contentDescription = "Home")
        }

        // Accounts Button
        IconButton(
            onClick = {
                navController.navigate("accounts/$userType") {
                    Log.d("Navigation","NavigatingTo:$email")
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
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
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}
