package com.example.bankapplication

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    bankViewModel: BankViewModel,
    userType: String,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val email by bankViewModel.currentCustomerEmail.collectAsState()
    var showChangePinDialog by remember { mutableStateOf(false) }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }
    var pinChangeError by remember { mutableStateOf<String?>(null) }
    var pinChangeSuccess by remember { mutableStateOf(false) }

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
            BottomNavigationBar(navController = navController, userType = userType, bankViewModel = bankViewModel)
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
            Text("Settings")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showChangePinDialog = true }) {
                Text("Change PIN")
            }

            Spacer(modifier = Modifier.height(16.dp))

            pinChangeError?.let {
                Text(it, color = Color.Red)
            }

            if (pinChangeSuccess) {
                Text("PIN changed successfully", color = Color.Green)
            }
        }

        // Change PIN Dialog
        if (showChangePinDialog) {
            ChangePinDialog(
                currentPin = currentPin,
                newPin = newPin,
                confirmNewPin = confirmNewPin,
                onCurrentPinChange = { currentPin = it },
                onNewPinChange = { newPin = it },
                onConfirmNewPinChange = { confirmNewPin = it },
                onConfirm = {
                    if (newPin == confirmNewPin) {
                        scope.launch {
                            if (currentPin.isNotBlank() && newPin.isNotBlank()) {
                                val newPinInt = newPin.toIntOrNull()
                                if (newPinInt != null) {
                                    val pinChanged = bankViewModel.changePin(
                                        email = email,
                                        currentPin = currentPin,
                                        newPin = newPinInt.toString()
                                    )
                                    if (pinChanged) {
                                        pinChangeSuccess = true
                                        pinChangeError = null
                                        Toast.makeText(context, "PIN changed successfully", Toast.LENGTH_SHORT).show()
                                        showChangePinDialog = false
                                    } else {
                                        pinChangeError = "Current PIN is incorrect."
                                        pinChangeSuccess = false
                                        Toast.makeText(context, "PIN change failed", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    pinChangeError = "New PIN must be a number."
                                    pinChangeSuccess = false
                                    Toast.makeText(context, "New PIN must be a number", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                pinChangeError = "Please fill in all fields."
                                pinChangeSuccess = false
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        pinChangeError = "New PINs do not match."
                        pinChangeSuccess = false
                        Toast.makeText(context, "New PINs do not match", Toast.LENGTH_SHORT).show()
                    }
                },
                onCancel = {
                    showChangePinDialog = false
                    currentPin = ""
                    newPin = ""
                    confirmNewPin = ""
                },
                errorMessage = pinChangeError
            )
        }
    }
}

@Composable
fun ChangePinDialog(
    currentPin: String,
    newPin: String,
    confirmNewPin: String,
    onCurrentPinChange: (String) -> Unit,
    onNewPinChange: (String) -> Unit,
    onConfirmNewPinChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    errorMessage: String?
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Change PIN") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = onCurrentPinChange,
                    label = { Text("Current PIN") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPin,
                    onValueChange = onNewPinChange,
                    label = { Text("New PIN") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmNewPin,
                    onValueChange = onConfirmNewPinChange,
                    label = { Text("Confirm New PIN") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Change PIN")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
