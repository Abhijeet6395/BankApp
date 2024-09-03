package com.example.bankapplication

import AccountsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.bankapplication.ui.theme.BankApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BankApplicationTheme {
                val navController = rememberNavController()
                val bankViewModel: BankViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginPage(navController = navController)
                    }
                    composable("enterPin/{userType}") { backStackEntry ->
                        EnterPinPage(
                            navController = navController,
                            userType = backStackEntry.arguments?.getString("userType") ?: ""
                        )
                    }
                    composable("bankerDetails/{email}") {
                        BankerDetailsScreen(
                            navController = navController,
                            bankViewModel = bankViewModel,

                        )
                    }
                    composable("customerDetails/{email}") {

                        CustomerDetailsScreen(
                            navController = navController,
                            bankViewModel = bankViewModel

                        )
                    }
                    composable("accounts/{userType}") { backStackEntry ->
                        AccountsScreen(
                            navController = navController,
                            bankViewModel = bankViewModel,
                            userType = backStackEntry.arguments?.getString("userType") ?: ""
                        )
                    }
                    composable("settings/{userType}") { backStackEntry ->
                        SettingsScreen(
                            navController = navController,
                            bankViewModel = bankViewModel,
                            userType = backStackEntry.arguments?.getString("userType") ?: "",

                        )
                    }
                }


            }
        }
    }


}

