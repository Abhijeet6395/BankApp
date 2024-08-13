package com.example.bankapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bankapplication.ui.theme.BankApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BankApplicationTheme {
                val navController = rememberNavController()
                val viewModel: BankViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginPage(navController = navController) }
                    composable("enterPin/{userType}") { backStackEntry ->
                        EnterPinPage(
                            navController = navController,
                            userType = backStackEntry.arguments?.getString("userType") ?: ""
                        )
                    }
                    composable("bankerDetails") {
                        BankerDetails(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("customerDetails") {
                        CustomerDetails(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            bankViewModel = viewModel
                        )
                    }
                    composable("accounts") {
                        AccountsScreen(
                            navController = navController,
                            bankViewModel = viewModel
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            bankViewModel = viewModel
                        )
                    }
                    composable("account") {
                        AccountScreen(
                            bankViewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
