package com.example.bankapplication

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@SuppressLint("SuspiciousIndentation")
@Composable

fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        containerColor = Color(0xFF6200EA),
        contentColor = Color.White
    ) {
        IconButton(
            onClick = {
                val currentUserType = navController.currentBackStackEntry?.arguments?.getString("userType")
                if (currentUserType == "banker") {
                    navController.navigate("bankerDetails")
                } else {
                    navController.navigate("customerDetails")
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(onClick = { navController.navigate("accounts") }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
        }
        IconButton(onClick = { navController.navigate("settings") }, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}


