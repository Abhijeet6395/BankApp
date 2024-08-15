package com.example.bankapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankerDetails(navController: NavController, viewModel: BankViewModel) {
    val users by viewModel.users.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }

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
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(users.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }) { user ->
                    UserListItem(user = user, onRemove = { viewModel.removeUser(user) })
                }
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
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newAccountType,
                                onValueChange = { newAccountType = it },
                                label = { Text("Account Type (Savings/Current)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = initialBalance,
                                onValueChange = { initialBalance = it },
                                label = { Text("Initial Balance") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (newUserName.isNotBlank() && newAccountType.isNotBlank() && initialBalance.isNotBlank()) {
                                val newUser = User(
                                    id = (users.size + 1).toString(),
                                    name = newUserName,
                                    accountType = newAccountType,
                                    balance = initialBalance.toDoubleOrNull() ?: 0.0
                                )
                                viewModel.addUser(newUser)
                                newUserName = ""
                                newAccountType = ""
                                initialBalance = ""
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
}

@Composable
fun UserListItem(user: User, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = user.name)
        IconButton(onClick = { onRemove() }) {
            Icon(Icons.Default.Delete, contentDescription = "Remove User")
        }
    }
}
@Composable
fun AddUserDialog(viewModel: BankViewModel) {
    var newUserName by remember { mutableStateOf("") }
    var newAccountType by remember { mutableStateOf("savings") } // Default to "savings" or "current"
    var showAddUserDialog by remember { mutableStateOf(false) }

    if (showAddUserDialog) {
        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text(text = "Add New User") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text("User Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAccountType,
                        onValueChange = { newAccountType = it },
                        label = { Text("Account Type (savings/current)") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newUserName.isNotBlank() && (newAccountType == "savings" || newAccountType == "current")) {
                        val newUser = User(
                            id = (viewModel.users.value.size + 1).toString(),
                            name = newUserName,
                            accountType = newAccountType,
                            balance = 0.0
                        )
                        viewModel.addUser(newUser)
                        newUserName = ""
                        newAccountType = "savings" // Reset account type to default
                        showAddUserDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddUserDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
