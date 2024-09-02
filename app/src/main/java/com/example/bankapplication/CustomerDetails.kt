package com.example.bankapplication

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    navController: NavController,
    bankViewModel: BankViewModel,
    email: String
) {

    val customers by bankViewModel.customers.collectAsState()
    val customer = customers[email]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Logout")
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userType = "customer",email=BankViewModel())
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            when (navController.currentBackStackEntry?.destination?.route) {
                "customerDetails/{email}" -> {
                    // Customer Details Section
                    customer?.let {
                        Text(
                            text = "Customer Details",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Name: ${it.name}")
                        Text(text = "Email: ${it.email}")
                        Text(text = "Account Type: ${it.account.accountType}")
                        Text(text = "Balance: ${it.account.balance}")
                    }
                }

                "customerAccounts" -> {
                    // Manage Accounts Section
                    ManageAccountsSection(bankViewModel = bankViewModel, email = BankViewModel())
                }

                "settings" -> {
                    // Settings Section
                    SettingsScreen(
                        navController = navController,
                        bankViewModel = bankViewModel,
                        userType = "customer",
                        email = BankViewModel()
                    )
                }
            }
        }
    }
}
