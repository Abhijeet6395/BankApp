package com.example.bankapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankerDetailsScreen(navController: NavController, bankViewModel: BankViewModel) {
    val bankManagers by bankViewModel.bankManagers.collectAsState(emptyMap())
    val managerEmail = bankManagers.keys.firstOrNull()
    val banker by remember(managerEmail) { derivedStateOf { managerEmail?.let { bankManagers[it] } } }
    LaunchedEffect(Unit) {
        bankViewModel.setCurrentCustomerEmail(navController.currentBackStackEntry?.arguments?.getString("email") ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Manager") },
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
            BottomNavigationBar(navController = navController, userType = "banker",bankViewModel=bankViewModel)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            banker?.let {
                if (navController.currentBackStackEntry?.destination?.route == "bankerDetails/{email}") {
                    Text(
                        text = "Bank Manager Details",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Name: ${it.name}")
                    Text(text = "Email: ${it.email}")
                    Text(text = "Branch: ${it.branch}")
                    Text(text = "Role: ${it.role}")
                }

                // Account Screen Content (Add/Remove Users)
                if (navController.currentBackStackEntry?.destination?.route == "bankerAccounts") {
                    ManageAccountsSection(bankViewModel = bankViewModel,email=BankViewModel())
                }

                // Settings Screen Content (Change PIN)
                if (navController.currentBackStackEntry?.destination?.route == "settings") {
                    SettingsScreen(
                        navController = navController,
                        bankViewModel = bankViewModel,
                        userType = "banker",
                        email = BankViewModel()
                    )
                }
            }
        }
    }
}

@Composable
fun BankManagerDetails(manager: BankManager) {
    Text(
        text = "Bank Manager Details",
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Name: ${manager.name}")
    Text(text = "Email: ${manager.email}")
    Text(text = "Branch: ${manager.branch}")
    Text(text = "Role: ${manager.role}")
}

@Composable
fun ManageAccountsSection(bankViewModel: BankViewModel,email: BankViewModel) {
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var newAccountNumber by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }

    Column {
        Text(text = "Manage Accounts", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
        TextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
        TextField(value = newPin, onValueChange = { newPin = it }, label = { Text("Pin") })

        TextField(
            value = newAccountType,
            onValueChange = { newAccountType = it },
            label = { Text("Account Type") })
        TextField(
            value = initialBalance,
            onValueChange = { initialBalance = it },
            label = { Text("Initial Balance") })

        Button(
            onClick = {
                bankViewModel.addCustomer(
                    name = newName,
                    email = newEmail,
                    pin = newPin.toIntOrNull().toString(),
                    accountType = newAccountType,
                    initialBalance = initialBalance.toDoubleOrNull() ?: 0.0
                )

                newName = ""
                newEmail = ""
                newPin = ""
                newAccountNumber = ""
                newAccountType = ""
                initialBalance = ""
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add Customer")
        }
    }
}
