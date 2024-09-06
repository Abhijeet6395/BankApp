import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapplication.Account
import com.example.bankapplication.BankViewModel
import com.example.bankapplication.BottomNavigationBar
import com.example.bankapplication.Customer
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
    val email by bankViewModel.currentCustomerEmail.collectAsState()
    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var showWithdrawMoneyDialog by remember { mutableStateOf(false) }
    var addAmount by remember { mutableStateOf("") }
    var withdrawAmount by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showRemoveDialog by remember { mutableStateOf(false) }
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
            BottomNavigationBar(navController = navController, userType = userType,bankViewModel=bankViewModel)
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
            if (userType == "customer") {
                Row {
                    Button(onClick = { showAddMoneyDialog = true }) {
                        Text("Add Money")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { showWithdrawMoneyDialog = true }) {
                        Text("Withdraw Money")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            } else if (userType == "banker") {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    item {
                        LaunchedEffect(key1 = bankViewModel.triggerRecomposition) {
                            // This will trigger recomposition when triggerRecomposition changes
                        }
                    }
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
                        items(customers.toList())  { (email, customer) ->
                            Text(
                                text = "${customer.name} (${customer.email})",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it },
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
                                    email = inputEmail,
                                    pin = pin,
                                    accountType = "Savings",
                                    initialBalance = 0.0
                                )
                                scope.launch {
                                    Toast.makeText(
                                        context,
                                        "Customer added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    inputEmail = ""
                                    name = ""
                                    pin = ""
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid email (must contain '@' and end with '.com')",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                                .show()
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
        }

        // Dialog for adding money
        if (showAddMoneyDialog) {
            AlertDialog(
                onDismissRequest = { showAddMoneyDialog = false },
                title = { Text("Add Money") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { inputEmail = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = addAmount,
                            onValueChange = { addAmount = it },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (email.isNotBlank() && addAmount.isNotBlank() && pin.isNotBlank()) {
                            val amount = addAmount.toDoubleOrNull()
                            if (isValidEmail(email)) {
                                if (amount != null && amount > 0) {
                                    if (bankViewModel.signIn(email, pin, "customer")) {
                                        bankViewModel.addMoney(email, amount)
                                        scope.launch {
                                            Toast.makeText(
                                                context,
                                                "Money added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        addAmount = ""
                                        inputEmail = ""
                                        pin = ""
                                        showAddMoneyDialog = false
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Credential is wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please enter a valid amount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { showAddMoneyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog for withdrawing money
        if (showWithdrawMoneyDialog) {
            AlertDialog(
                onDismissRequest = { showWithdrawMoneyDialog = false },
                title = { Text("Withdraw Money") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { inputEmail = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = withdrawAmount,
                            onValueChange = { withdrawAmount = it },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (email.isNotBlank() && withdrawAmount.isNotBlank() && pin.isNotBlank()) {
                            val amount = withdrawAmount.toDoubleOrNull()
                            if (isValidEmail(email)) {
                                if (amount != null && amount > 0) {
                                    if (bankViewModel.signIn(email, pin, "customer")) {
                                        val customer = bankViewModel.customers.value[email]
                                        if (customer != null && customer.account.balance >= amount) {
                                            bankViewModel.withdrawMoney(email, amount)
                                            scope.launch {
                                                Toast.makeText(
                                                    context,
                                                    "Money withdrawn successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            withdrawAmount = ""
                                            inputEmail = ""
                                            pin = ""
                                            showWithdrawMoneyDialog = false
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Insufficient balance",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Credential is wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please enter a valid amount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                        Text("Withdraw")
                    }
                },
                dismissButton = {
                    Button(onClick = { showWithdrawMoneyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog for removing customer
        if (showRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text("Remove Customer") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = removeEmail,
                            onValueChange = { removeEmail = it },
                            label = { Text("Email ID") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (removeError.isNotEmpty()) {
                            Text(text = removeError, color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (removeEmail.isNotBlank()) {
                            if (isValidEmail(removeEmail)) {
                                val customer = bankViewModel.customers.value[removeEmail]
                                if (customer != null) {
                                    bankViewModel.removeCustomer(customer.name, removeEmail)
                                    scope.launch {
                                        Toast.makeText(
                                            context,
                                            "Customer removed successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    removeEmail = ""
                                    showRemoveDialog = false
                                } else {
                                    removeError = "Customer not found"
                                }
                            } else {
                                removeError = "Please enter a valid email"
                            }
                        } else {
                            removeError = "Please enter an email"
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