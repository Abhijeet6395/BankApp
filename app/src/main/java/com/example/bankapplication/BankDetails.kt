package com.example.bankapplication

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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankerDetailsScreen(navController: NavController, bankViewModel: BankViewModel = viewModel()) {
    val bankManager by bankViewModel.bankManager.collectAsState()

    LaunchedEffect(Unit) {
        val email = navController.currentBackStackEntry?.arguments?.getString("email") ?: ""
        bankViewModel.getBankManagerDetails(email)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Manager") },
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
                userType = "banker",
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
                "bankerDetails/{email}" -> {
                    bankManager?.let {
                        BankManagerDetails(manager = it)
                    }
                }

                "bankerAccounts" -> {
                    ManageAccountsSection(
                        bankViewModel = bankViewModel,
                        email = String.toString()
                    )
                }

                "settings" -> {
                    SettingsScreen(
                        navController = navController,
                        bankViewModel = bankViewModel,
                        userType = "banker"
                    )
                }
            }
        }
    }
}

@Composable
fun BankManagerDetails(manager: BankManager) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Manager Icon",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bank Manager Details",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Name: ${manager.name}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Email: ${manager.email}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Branch: ${manager.branch}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Role: ${manager.role}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Divider(thickness = 1.dp, color = Color.Gray)
    }
}

@Composable
fun ManageAccountsSection(bankViewModel: BankViewModel, email: String) {
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }
    val newCoroutineScope =rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Manage Accounts",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newPin,
            onValueChange = { newPin = it },
            label = { Text("Pin") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newAccountType,
            onValueChange = { newAccountType = it },
            label = { Text("Account Type") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = initialBalance,
            onValueChange = { initialBalance = it },
            label = { Text("Initial Balance") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                newCoroutineScope.launch {
                    bankViewModel.addCustomer(
                        name = newName,
                        email = newEmail,
                        pin = newPin,
                        accountType = newAccountType,
                        initialBalance = initialBalance.toDoubleOrNull() ?: 0.0
                    )
                }
                newName = ""
                newEmail = ""
                newPin = ""
                newAccountType = ""
                initialBalance = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Add Customer")
        }
    }
}
