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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bankapplication.AccountType
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
    val selectedAccountType by remember { mutableStateOf(AccountType.SAVINGS) }
    var accountType by remember { mutableStateOf(AccountType.SAVINGS.name) }
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
                title = {
                    Text(
                        "Accounts",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },

                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.primary
                ),
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
                .background(Color(0xFFE0E0E0))
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
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black
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
                            Text(
                                text = "${customer.name} (${customer.email})",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it },
                    label = { Text("Email ID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults
                        .colors(focusedTextColor = Color.Black),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults
                        .colors(focusedTextColor = Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = accountType,
                    onValueChange = {
                        accountType = it
                    },
                    label = { Text("Account Type (SAVINGS or CURRENT)") },
                    isError = accountType !in listOf(
                        AccountType.SAVINGS.name,
                        AccountType.CURRENT.name
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black)
                )
                Row {
                    Button(onClick = {
                        if (email.isNotBlank() && name.isNotBlank() && pin.isNotBlank() && accountType.isNotBlank()) {
                            if (isValidEmail(email)) {
                                if (accountType in listOf(
                                        AccountType.SAVINGS.name,
                                        AccountType.CURRENT.name
                                    )
                                ) {
                                    scope.launch {
                                        val accountType = when (selectedAccountType) {
                                            AccountType.SAVINGS -> AccountType.SAVINGS
                                            AccountType.CURRENT -> AccountType.CURRENT
                                        }
                                        val isCustomerAdded = bankViewModel.addCustomer(
                                            name = name,
                                            email = inputEmail,
                                            pin = pin,
                                            accountType = accountType.toString(),
                                            initialBalance = 0.0
                                        )
                                        if (isCustomerAdded) {
                                            Toast.makeText(
                                                context,
                                                "Customer added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            inputEmail = ""
                                            name = ""
                                            pin = ""
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Email already exists",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid account type. Please enter SAVINGS or CURRENT.",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                        scope.launch {
                            if (addAmount.isNotBlank() && pin.isNotBlank()) {
                                val amount = addAmount.toDoubleOrNull()
                                if (amount != null && amount > 0) {
                                    val signInSuccess = bankViewModel.signIn(email, pin, "customer")
                                    if (signInSuccess) {
                                        bankViewModel.addMoney(email, amount)
                                        val cust = customer.find { it.email == email }
                                        cust?.let {
                                            val account =
                                                bankViewModel.getAccountByAccountNumber(it.accountNumber)
                                            balance = account?.balance ?: 0.0
                                        }
                                        Toast.makeText(
                                            context,
                                            "Money added successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        addAmount = ""
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
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
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
                        scope.launch {
                            if (withdrawAmount.isNotBlank() && pin.isNotBlank()) {
                                val amount = withdrawAmount.toDoubleOrNull()
                                if (amount != null && amount > 0) {
                                    val signInSuccess = bankViewModel.signIn(email, pin, "customer")
                                    if (signInSuccess) {
                                        bankViewModel.withdrawMoney(email, amount)
                                        val cust = customer.find { it.email == email }
                                        cust?.let {
                                            val account =
                                                bankViewModel.getAccountByAccountNumber(it.accountNumber)
                                            balance = account?.balance ?: 0.0
                                        }
                                        Toast.makeText(
                                            context,
                                            "Money withdrawn successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        withdrawAmount = ""
                                        pin = ""
                                        showWithdrawMoneyDialog = false
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
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
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

        // Dialog for removing a customer
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
                        if (removeError.isNotBlank()) {
                            Text(
                                text = removeError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            if (removeEmail.isNotBlank()) {
                                if (isValidEmail(removeEmail)) {
                                    val result = bankViewModel.removeCustomer(removeEmail)
                                    if (result) {
                                        Toast.makeText(
                                            context,
                                            "Customer removed successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        removeEmail = ""
                                        showRemoveDialog = false
                                    } else {
                                        removeError = "Failed to remove customer"
                                    }
                                } else {
                                    removeError = "Invalid email format"
                                }
                            } else {
                                removeError = "Email field cannot be empty"
                            }
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
