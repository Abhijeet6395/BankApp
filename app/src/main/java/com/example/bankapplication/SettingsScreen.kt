package com.example.bankapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    bankViewModel: BankViewModel,
    userType: String,
    email: BankViewModel
) {
    var email by remember { mutableStateOf("") }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
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
            BottomNavigationBar(navController = navController, userType = userType,email= BankViewModel())
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
            Text("Change PIN")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email ID") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentPin,
                onValueChange = { currentPin = it },
                label = { Text("Current PIN") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPin,
                onValueChange = { newPin = it },
                label = { Text("New PIN") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (email.isNotBlank() && currentPin.isNotBlank() && newPin.isNotBlank()) {
                    val newPinInt = newPin.toIntOrNull()
                    if (newPinInt != null) {
                        val pinChanged = bankViewModel.changePin(
                            email = email,
                            currentPin = currentPin,
                            newPin = newPinInt
                        )
                        if (pinChanged) {
                            pinChangeSuccess = true
                            pinChangeError = null
                        } else {
                            pinChangeError = "Current PIN is incorrect."
                            pinChangeSuccess = false
                        }
                    } else {
                        pinChangeError = "New PIN must be a number."
                        pinChangeSuccess = false
                    }
                } else {
                    pinChangeError = "Please fill in all fields."
                    pinChangeSuccess = false
                }
            }) {
                Text("Apply")
            }

            Spacer(modifier = Modifier.height(16.dp))

            pinChangeError?.let {
                Text(it, color = Color.Red)
            }

            if (pinChangeSuccess) {
                Text("PIN changed successfully", color = Color.Green)
            }
        }
    }
}
