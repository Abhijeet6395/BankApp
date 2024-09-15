package com.example.bankapplication

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    navController: NavController,
    bankViewModel: BankViewModel= viewModel(),

    ) {


    LaunchedEffect(key1 = bankViewModel.customers) {
        bankViewModel.setCurrentCustomerEmail(
            navController.currentBackStackEntry?.arguments?.getString(
                "email"
            ) ?: ""
        )
    }

    val customers by bankViewModel.customers.collectAsState()
    val email by bankViewModel.currentCustomerEmail.collectAsState()
    val customer = customers.find { it.email == email }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") },
                actions = {
                    IconButton(onClick = {
                        bankViewModel.onLogoutComplete()
                        navController.navigate("login") {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Logout")
                    }
                },
            )
        },
        bottomBar = {

            BottomNavigationBar(
                navController = navController,
                userType = "customer",
                bankViewModel = bankViewModel
            )
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
                    Log.d("CustomerDetailsScreen", "Customer: $email")
                    customer?.let {
                        Text(
                            text = "Customer Details",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Name: ${it.name}")
                        Text(text = "Email: ${it.email}")
                        Text(text = "Account Number: ${it.accountNumber}")
                        Text(text = "Account Type: ${it.accountType}")
                    }
                }


                "customerAccounts" -> {
                    // Manage Accounts Section
                    ManageAccountsSection(bankViewModel = bankViewModel, email = email)
                }

                "settings" -> {
                    // Settings Section
                    SettingsScreen(
                        navController = navController,
                        bankViewModel = bankViewModel,
                        userType = "customer"
                    )
                }
            }
        }
    }
}