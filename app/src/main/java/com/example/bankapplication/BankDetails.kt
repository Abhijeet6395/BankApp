package com.example.bankapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankerDetailsScreen(navController: NavController, bankViewModel: BankViewModel = viewModel()) {
    val bankManager by bankViewModel.bankManager.collectAsState()

    // Trigger fetching of bank manager details when the screen is launched
    LaunchedEffect(Unit) {
        val email = navController.currentBackStackEntry?.arguments?.getString("email") ?: ""
        bankViewModel.getBankManagerDetails(email)
    }

    // Gradient background color for the entire screen

        // Scaffold layout with TopAppBar and BottomAppBar
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Bank Manager",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
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
                            Icon(Icons.Filled.Clear, contentDescription = "Logout", tint = Color.White)
                        }
                    }
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
                    .background(Color(0xFFE0E0E0))
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Display relevant content based on current navigation route
                when (navController.currentBackStackEntry?.destination?.route) {
                    "bankerDetails/{email}" -> {
                        bankManager?.let {
                            BankManagerDetails(manager = it)
                        }
                    }

                    "bankerAccounts" -> {
                        ManageAccountsSection(
                            bankViewModel = bankViewModel,
                            email = bankManager?.email ?: ""
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


// Composable to display bank manager's details with enhanced styling
@Composable
fun BankManagerDetails(manager: BankManager) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile icon with shadow and circle background
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Manager Icon",
            modifier = Modifier
                .size(80.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bank Manager Details",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Name: ${manager.name}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Text(text = "Email: ${manager.email}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Text(text = "Branch: ${manager.branch}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Text(text = "Role: ${manager.role}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    }
}

// Section to manage customer accounts with improved UI design
@Composable
fun ManageAccountsSection(bankViewModel: BankViewModel, email: String) {
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }
    val newCoroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(Color.White, shape = RoundedCornerShape(12.dp))
        .padding(16.dp)) {
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
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add Customer", color = Color.White)
        }
    }
}
