package com.example.bankapplication

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    navController: NavController,
    bankViewModel: BankViewModel = viewModel()
) {
    LaunchedEffect(key1 = bankViewModel.customers) {
        bankViewModel.setCurrentCustomerEmail(
            navController.currentBackStackEntry?.arguments?.getString("email") ?: ""
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (navController.currentBackStackEntry?.destination?.route) {
                "customerDetails/{email}" -> {
                    customer?.let {
                        CustomerDetailsView(customer = it)
                    }
                }

                "customerAccounts" -> {
                    ManageAccountsSection(bankViewModel = bankViewModel, email = email)
                }

                "settings" -> {
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

@Composable
fun CustomerDetailsView(customer: Customer) {
    Icon(
        imageVector = Icons.Filled.Person,
        contentDescription = "Customer Icon",
        modifier = Modifier.size(80.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Customer Details",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Name: ${customer.name}", style = MaterialTheme.typography.bodyLarge)
    Text(text = "Email: ${customer.email}", style = MaterialTheme.typography.bodyLarge)
    Text(
        text = "Account Number: ${customer.accountNumber}",
        style = MaterialTheme.typography.bodyLarge
    )
    Text(text = "Account Type: ${customer.accountType}", style = MaterialTheme.typography.bodyLarge)
    Spacer(modifier = Modifier.height(24.dp))
    Divider(thickness = 1.dp, color = Color.Gray)
}
