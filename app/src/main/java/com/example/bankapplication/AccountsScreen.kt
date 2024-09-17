import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
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
    val customer by bankViewModel.customers.collectAsState()
    var balance by remember { mutableDoubleStateOf(0.0) }

    // Function to validate email format
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.endsWith(".com")
    }

    LaunchedEffect(email) {
        val cust = customer.find { it.email == email }
        cust?.let {
            val account = bankViewModel.getAccountByAccountNumber(it.accountNumber)
            balance = account?.balance ?: 0.0
        }
    }

    LaunchedEffect(key1 = bankViewModel.triggerRecomposition) {
        val cust = customer.find { it.email == email }
        cust?.let {
            val account = bankViewModel.getAccountByAccountNumber(it.accountNumber)
            balance = account?.balance ?: 0.0
        }
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
            BottomNavigationBar(
                navController = navController,
                userType = userType,
                bankViewModel = bankViewModel
            )
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

                Text(
                    text = "Current Balance: $balance",
                    style = MaterialTheme.typography.headlineMedium
                )

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
                        items(customers) { customer ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Name: ${customer.name}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Email: ${customer.email}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 16.sp
                                    )
                                }
                            }
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
                        if (inputEmail.isNotBlank() && name.isNotBlank() && pin.isNotBlank()) {
                            if (isValidEmail(inputEmail)) {
                                scope.launch {
                                    bankViewModel.addCustomer(
                                        name = name,
                                        email = inputEmail,
                                        pin = pin,
                                        accountType = "Savings",
                                        initialBalance = 0.0
                                    )
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

        // Dialogs (Add Money, Withdraw Money, Remove Customer)
        if (showAddMoneyDialog) {
            AddMoneyDialog(
                addAmount = addAmount,
                onAddAmountChange = { addAmount = it },
                onConfirm = {
                    scope.launch {
                        val currentCustomer = customers.find { it.email == email }
                        currentCustomer?.let { customer ->
                            val account = bankViewModel.getAccountByAccountNumber(customer.accountNumber)
                            account?.let {
                                bankViewModel.addMoney(account.accountNumber, addAmount.toDouble())
                                Toast.makeText(context, "Money added successfully", Toast.LENGTH_SHORT).show()
                                showAddMoneyDialog = false
                            }
                        }
                    }
                },
                onCancel = { showAddMoneyDialog = false }
            )
        }

        if (showWithdrawMoneyDialog) {
            WithdrawMoneyDialog(
                withdrawAmount = withdrawAmount,
                onWithdrawAmountChange = { withdrawAmount = it },
                onConfirm = {
                    scope.launch {
                        val currentCustomer = customers.find { it.email == email }
                        currentCustomer?.let { customer ->
                            val account = bankViewModel.getAccountByAccountNumber(customer.accountNumber)
                            account?.let {
                                bankViewModel.withdrawMoney(account.accountNumber, withdrawAmount.toDouble())
                                Toast.makeText(context, "Money withdrawn successfully", Toast.LENGTH_SHORT).show()
                                showWithdrawMoneyDialog = false
                            }
                        }
                    }
                },
                onCancel = { showWithdrawMoneyDialog = false }
            )
        }

        if (showRemoveDialog) {
            RemoveCustomerDialog(
                removeEmail = removeEmail,
                onRemoveEmailChange = { removeEmail = it },
                onConfirm = {
                    scope.launch {
                        bankViewModel.removeCustomer(removeEmail)
                        Toast.makeText(context, "Customer removed successfully", Toast.LENGTH_SHORT).show()
                        showRemoveDialog = false
                    }
                },
                onCancel = { showRemoveDialog = false },
                errorMessage = removeError
            )
        }
    }
}

@Composable
fun AddMoneyDialog(
    addAmount: String,
    onAddAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Add Money") },
        text = {
            OutlinedTextField(
                value = addAmount,
                onValueChange = onAddAmountChange,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WithdrawMoneyDialog(
    withdrawAmount: String,
    onWithdrawAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Withdraw Money") },
        text = {
            OutlinedTextField(
                value = withdrawAmount,
                onValueChange = onWithdrawAmountChange,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Withdraw")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RemoveCustomerDialog(
    removeEmail: String,
    onRemoveEmailChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    errorMessage: String
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Remove Customer") },
        text = {
            Column {
                OutlinedTextField(
                    value = removeEmail,
                    onValueChange = onRemoveEmailChange,
                    label = { Text("Email ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Remove")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

