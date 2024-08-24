import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapplication.BankViewModel
import com.example.bankapplication.BottomNavigationBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    navController: NavController,
    bankViewModel: BankViewModel,
    userType: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val customers by bankViewModel.customers.collectAsState()

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var removeName by remember { mutableStateOf("") }
    var removeEmail by remember { mutableStateOf("") }
    var removeError by remember { mutableStateOf("") }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.endsWith(".com")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userType = userType)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Customers")

            Spacer(modifier = Modifier.height(8.dp))

            // Display customers in a LazyColumn
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (customers.isEmpty()) {
                    item {
                        Text(
                            text = "No users present",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(customers.toList()) { (email, customer) ->
                        Text(
                            text = "${customer.name} (${customer.email})",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input fields and buttons for adding customers
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    if (email.isNotBlank() && name.isNotBlank() && pin.isNotBlank()) {
                        if (isValidEmail(email)) {
                            bankViewModel.addCustomer(
                                name = name,
                                email = email,
                                pin = pin,
                                accountType = "Savings",
                                initialBalance = 0.0
                            )
                            scope.launch {
                                Toast.makeText(context, "Customer added successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please enter a valid email (must contain '@' and end with '.com')", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Add Customer")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    showRemoveDialog = true
                }) {
                    Text("Remove Customer")
                }
            }
        }

        // Dialog for removing a customer
        if (showRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text("Remove Customer") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = removeName,
                            onValueChange = { removeName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = removeEmail,
                            onValueChange = { removeEmail = it },
                            label = { Text("Email ID") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (removeError.isNotBlank()) {
                            Text(
                                text = removeError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (removeName.isNotBlank() && removeEmail.isNotBlank()) {
                            val customerToRemove = customers[removeEmail]
                            if (customerToRemove != null && customerToRemove.name == removeName) {
                                bankViewModel.removeCustomer(removeName, removeEmail)
                                scope.launch {
                                    Toast.makeText(context, "Customer removed successfully", Toast.LENGTH_SHORT).show()
                                }
                                removeError = ""
                                showRemoveDialog = false
                            } else {
                                removeError = "No customer found with provided details"
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    Button(onClick = { showRemoveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
