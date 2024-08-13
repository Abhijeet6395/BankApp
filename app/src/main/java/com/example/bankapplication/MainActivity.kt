package com.example.bankapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
                            viewModel = BankViewModel()
                        )
                    }
                    composable("customerDetails") {
                        CustomerDetails(
                            navController = navController,
                            viewModel = BankViewModel()
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            bankViewModel = BankViewModel()
                        )
                    }
                    composable("accounts") {
                        AccountsScreen(
                            navController = navController,
                            bankViewModel = BankViewModel()
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            bankViewModel = BankViewModel()
                        )
                    }
                    composable("account") {
                        AccountScreen(
                            bankViewModel = BankViewModel(),
                            navController = navController
                        )
                    }
                }
            }
        }
    }


}




