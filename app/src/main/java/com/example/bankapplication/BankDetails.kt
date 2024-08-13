package com.example.bankapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


data class User(val id: String, val name: String)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankerDetails(navController: NavController, viewModel: BankViewModel) {
    val userNavController = rememberNavController()
    val users by viewModel.users.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var userToRemove by remember { mutableStateOf<User?>(null) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Banker Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF6200EA),
                contentColor = Color.White
            ) {
                IconButton(
                    onClick = { userNavController.navigate("home") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                IconButton(
                    onClick = { userNavController.navigate("accounts") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
                }
                IconButton(
                    onClick = { userNavController.navigate("settings") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query -> searchQuery = query },
                    label = { Text("Search User") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(users.filter {
                        it.name.contains(
                            searchQuery,
                            ignoreCase = true
                        )
                    }) { user ->
                        UserListItem(
                            user = user,
                            onRemove = {
                                userToRemove = user
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    userToRemove?.let {
                        viewModel.removeUser(it)
                        userToRemove = null
                    }
                }) {
                    Text("Remove User")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showAddUserDialog = true }) {
                    Text("Add New User")
                }

                if (showAddUserDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddUserDialog = false },
                        title = { Text("Add New User") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = newUserName,
                                    onValueChange = { newUserName = it },
                                    label = { Text("User Name") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (newUserName.isNotBlank()) {
                                    val newUser =
                                        User(id = (users.size + 1).toString(), name = newUserName)
                                    viewModel.addUser(newUser)
                                    newUserName = ""
                                    showAddUserDialog = false
                                }
                            }) {
                                Text("Add")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddUserDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun UserListItem(user: User, onRemove: (Any?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = user.name)
        IconButton(onClick = { onRemove(user) }) {
            Icon(Icons.Default.Delete, contentDescription = "Remove User")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetails(navController: NavController, viewModel: BankViewModel) {
    val customer = remember {
        mutableStateOf(
            Customer(
                id = "1",
                name = "Coa wali back",
                accountNumber = "123456789",
                balance = viewModel.accountBalance.intValue.toDouble()
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF6200EA),
                contentColor = Color.White
            ) {
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                IconButton(
                    onClick = { navController.navigate("account") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                }
                IconButton(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Customer Details",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Name: ${customer.value.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Account Number: ${customer.value.accountNumber}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Balance: $${viewModel.accountBalance.intValue}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}


data class Customer(
    val id: String,
    val name: String,
    val accountNumber: String,
    val balance: Double
)

@Composable
fun AccountsScreen(navController: NavController,bankViewModel: BankViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Accounts Screen",
            color = Color.Red
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    bankViewModel: BankViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF6200EA),
                contentColor = Color.White
            ) {
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                IconButton(
                    onClick = { navController.navigate("accounts") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
                }
                IconButton(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    bankViewModel: BankViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF6200EA),
                contentColor = Color.White
            ) {
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                IconButton(
                    onClick = { navController.navigate("accounts") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
                }
                IconButton(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        },
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(bankViewModel: BankViewModel,navController: NavController) {
    var amount by remember { mutableStateOf("0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF6200EA),
                contentColor = Color.White
            ) {
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                IconButton(
                    onClick = { navController.navigate("accounts") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Accounts")
                }
                IconButton(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }

            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Account Balance: $${bankViewModel.accountBalance.intValue}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        bankViewModel.addMoney(amount.toIntOrNull() ?: 0)
                    }) {
                        Text("Add Money")
                    }

                    Button(onClick = {
                        bankViewModel.withdrawMoney(amount.toIntOrNull() ?: 0)
                    }) {
                        Text("Withdraw Money")
                    }
                }
            }
        }
    )
}
