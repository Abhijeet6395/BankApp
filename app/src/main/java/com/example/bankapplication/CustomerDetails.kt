package com.example.bankapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(navController: NavController, bankViewModel: BankViewModel = viewModel()) {
    val customers by bankViewModel.customers.collectAsState(emptyMap())
    val customerEmail = customers.keys.firstOrNull()
    val customer by remember(customerEmail) { derivedStateOf { customerEmail?.let { customers[it] } } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userType = "customer")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            customer?.let {
                if (navController.currentBackStackEntry?.destination?.route == "customerDetails") {
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

                if (navController.currentBackStackEntry?.destination?.route == "customerAccounts") {
                    ManageAccountsSection(bankViewModel = bankViewModel)
                }

                if (navController.currentBackStackEntry?.destination?.route == "settings") {
                    SettingsScreen(
                        navController = navController,
                        bankViewModel = bankViewModel,
                        userType = "customer",
                        currentUserEmail = toString()
                    )
                }
            }
        }
    }
}

